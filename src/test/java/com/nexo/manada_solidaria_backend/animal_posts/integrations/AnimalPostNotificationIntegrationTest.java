package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.TransitionStatusRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.*;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.*;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseCommittedNotificationIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.notifications.clients.WebPushClient;
import com.nexo.manada_solidaria_backend.notifications.models.data.*;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnimalPostNotificationIntegrationTest extends BaseCommittedNotificationIntegrationTest {

    @Test
    @DisplayName("Las notificaciones esperan el commit y no se envian si el POST se revierte")
    void create_rollbackDoesNotSendNotifications() throws Exception {
        seedSimilarLostPetScenario();
        givenPushResponds(201);
        postAnimalPost(MockAnimalPostDataUtils.LOST_STREET_WITHOUT_PHONE)
                .andExpect(status().isCreated());

        assertThat(notificationDeliveryRepository.count()).isZero();
        verifyNoInteractions(webPushClient);

        TestTransaction.flagForRollback();
        TestTransaction.end();
        TestTransaction.start();

        assertThat(animalPostRepository.count()).isZero();
        assertThat(notificationDeliveryRepository.count()).isZero();
        verifyNoInteractions(webPushClient);
    }

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#";

    @Autowired
    private AnimalPostRepository animalPostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;
    @Autowired
    private PushSuscriptionRepository pushSuscriptionRepository;

    @MockitoBean
    private WebPushClient webPushClient;

    @DisplayName("POST /animal-posts de un animal en la calle notifica por push a quien busca uno del mismo tipo")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideSimilarLostPetRecipientCases")
    void create_lost_notifiesOwnersSearchingSameAnimalType(String testName, String body, String username, int expectedDeliveries) throws Exception {
        seedSimilarLostPetScenario();
        givenPushResponds(201);

        postAnimalPost(body).andExpect(status().isCreated());
        commitAndStartReadTransaction();

        assertThat(similarLostPetDeliveriesOf(username)).hasSize(expectedDeliveries);
    }

    @Test
    @DisplayName("POST /animal-posts en la calle: el push lleva la direccion y el link al post, y queda SENT")
    void create_lost_sendsPushWithAddressAndLink() throws Exception {
        seedSimilarLostPetScenario();
        givenPushResponds(201);

        String response = postAnimalPost(MockAnimalPostDataUtils.LOST_STREET_WITHOUT_PHONE)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String postId = mapper.readTree(response).get("id").asText();

        commitAndStartReadTransaction();

        NotificationDelivery delivery = similarLostPetDeliveriesOf("busca-perro").getFirst();
        NotificationPayload payload = delivery.getPayload();
        assertThat(payload.getMessage()).isEqualTo("Vieron un animal en Corrientes 500, Buenos Aires. Revisá si puede ser tu mascota perdida.");
        assertThat(delivery.getStatusHistory().getLast().getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    @DisplayName("POST /animal-posts en la calle: si falla el push el post se crea igual y la entrega queda FAILED")
    void create_lost_whenPushFails_createsPostAndMarksDeliveryFailed() throws Exception {
        seedSimilarLostPetScenario();
        given(webPushClient.send(any(), any())).willThrow(new IOException("push caido"));

        postAnimalPost(MockAnimalPostDataUtils.LOST_STREET_WITHOUT_PHONE).andExpect(status().isCreated());

        commitAndStartReadTransaction();

        assertThat(similarLostPetDeliveriesOf("busca-perro").getFirst().getStatusHistory().getLast().getStatus())
                .isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    @DisplayName("POST /animal-posts en la calle: notifica por campanita aunque el usuario no tenga push")
    void create_lost_notifiesInAppWithoutPushSubscription() throws Exception {
        seedSimilarLostPetScenario();
        givenPushResponds(201);
        User withoutPush = userRepository.save(new User("busca-perro-sin-push", "x",
                new Profile("busca-perro-sin-push@mail.com", new PhoneNumber("353", "4014524"), Set.of(Rol.COMMUNITY))));
        saveLostPostOf(withoutPush, AnimalType.DOG, StatusLostPost.SEARCHING, true);

        postAnimalPost(MockAnimalPostDataUtils.LOST_STREET_WITHOUT_PHONE).andExpect(status().isCreated());

        commitAndStartReadTransaction();

        assertThat(similarLostPetDeliveriesOf("busca-perro-sin-push", NotificationChannel.PUSH)).isEmpty();
        assertThat(similarLostPetDeliveriesOf("busca-perro-sin-push", NotificationChannel.IN_APP))
                .singleElement()
                .satisfies(delivery -> assertThat(delivery.getStatusHistory().getLast().getStatus()).isEqualTo(NotificationStatus.SENT));
    }

    @DisplayName("POST /animal-posts en la calle: el color solo descarta cuando los dos estan cargados y son distintos")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideSimilarLostPetColorCases")
    void create_street_matchesColorOnlyWhenBothAreLoaded(
            String testName,
            String streetColor,
            String searchedColor,
            int expectedDeliveries
    ) throws Exception {
        seedSimilarLostPetScenario();
        givenPushResponds(201);
        saveLostPostOf(subscribedUser("busca-por-color"), AnimalType.DOG, searchedColor, StatusLostPost.SEARCHING, true);

        postAnimalPost(MockAnimalPostDataUtils.streetDogWithColor(streetColor)).andExpect(status().isCreated());

        commitAndStartReadTransaction();

        assertThat(similarLostPetDeliveriesOf("busca-por-color")).hasSize(expectedDeliveries);
    }

    @DisplayName("POST /animal-posts avisa a los hogares de transito, menos al autor, solo si la adopcion busca transito")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideTransitNotificationCreateCases")
    @Sql(statements = "INSERT INTO profile_roles (profile_id, role) VALUES ('11111111-1111-1111-1111-111111111111', 'TRANSITIONAL_HOME')")
    void create_notifiesTransitHomesWhenSearchingTransit(String testName, String body, String username, int expectedDeliveries) throws Exception {
        seedTransitScenario();

        postAnimalPost(body).andExpect(status().isCreated());

        commitAndStartReadTransaction();

        assertThat(transitDeliveriesOf(username)).hasSize(expectedDeliveries);
    }

    @DisplayName("PATCH /animal-posts/{id}/status avisa a los hogares de transito solo si la adopcion pasa a buscar transito")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideTransitNotificationTransitionCases")
    void transitionStatus_notifiesTransitHomesWhenSearchingTransit(
            String testName,
            String startStatus,
            String targetStatus,
            int expectedDeliveries
    ) throws Exception {
        seedTransitScenario();
        AdoptionPost post = saveAdoptionPost("Adopcion", StatusAdoptionPost.valueOf(startStatus));

        patchStatus(post.getId(), targetStatus).andExpect(status().isOk());

        commitAndStartReadTransaction();

        assertThat(transitDeliveriesOf("transito")).hasSize(expectedDeliveries);
    }

    @Test
    @DisplayName("El aviso de transito en la campanita lleva el texto y el link al post, y queda SENT")
    void transitNotification_hasTextAndLinkToPost() throws Exception {
        seedTransitScenario();

        String response = postAnimalPost(MockAnimalPostDataUtils.ADOPTION_VALID)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String postId = mapper.readTree(response).get("id").asText();

        commitAndStartReadTransaction();

        NotificationDelivery delivery = transitDeliveriesOf("transito").getFirst();
        NotificationPayload payload = delivery.getPayload();
        assertThat(payload.getTitle()).isEqualTo("🏠 Un animal necesita hogar de tránsito");
        assertThat(payload.getMessage()).isEqualTo("Hay un animal buscando tránsito. ¿Podés recibirlo?");
        assertThat(delivery.getStatusHistory().getLast().getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    private void seedTransitScenario() {
        notificationRepository.save(new Notification(
                "🏠 Un animal necesita hogar de tránsito",
                "Hay un animal buscando tránsito. ¿Podés recibirlo?",
                null,
                NotificationType.IN_ADOPTION_AND_TRANSIT_PET
        ));
        saveUserWithRole("transito", Rol.TRANSITIONAL_HOME);
        saveUserWithRole("comunidad", Rol.COMMUNITY);
    }

    private void saveUserWithRole(String username, Rol role) {
        userRepository.save(new User(username, "x",
                new Profile(username + "@mail.com", new PhoneNumber("353", "4014524"), Set.of(role))));
    }

    private List<NotificationDelivery> transitDeliveriesOf(String username) {
        User recipient = userRepository.findByUsername(username).orElseThrow();
        return notificationDeliveryRepository.findAllByRecipientAndChannelOrderByCreatedAtDesc(recipient, NotificationChannel.IN_APP)
                .stream()
                .filter(delivery -> delivery.getType() == NotificationType.IN_ADOPTION_AND_TRANSIT_PET)
                .toList();
    }

    private ResultActions patchStatus(UUID postId, String targetStatus) throws Exception {
        return mockMvc.perform(
                patch("/animal-posts/" + postId + "/status")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new TransitionStatusRequest(targetStatus)))
        );
    }

    private AdoptionPost saveAdoptionPost(String name, StatusAdoptionPost status) {
        return saveAdoptionPost(name, status, animal());
    }

    private AdoptionPost saveAdoptionPost(String name, StatusAdoptionPost status, Animal animal) {
        AdoptionPost post = new AdoptionPost(name, "Descripción", "cf-img", null, new PhoneNumber("3533", "436249"), admin(), animal, location(), false);
        post.setStatusHistory(new ArrayList<>(List.of(new AdoptionPostStatusHistory(status, post))));
        return animalPostRepository.save(post);
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private Animal animal() {
        return animal(AnimalType.DOG, AnimalSize.MEDIUM, AnimalGender.MALE, null, null);
    }

    private Animal animal(AnimalType type, AnimalSize size, AnimalGender gender, AnimalAge age, String color) {
        return new Animal(color, age, size, gender, type);
    }

    private Location location() {
        return new Location("Argentina", "Villa María", "Parque", "Av. Patricias 100", "Av. Patricias", 100, -34.6, -58.4);
    }

    private void seedSimilarLostPetScenario() {
        notificationRepository.save(new Notification(
                "🚨 Vieron un animal en la calle",
                "Vieron un animal en {location}. Revisá si puede ser tu mascota perdida.",
                null,
                NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST
        ));
        saveLostPostOf(subscribedUser("busca-perro"), AnimalType.DOG, StatusLostPost.SEARCHING, true);
        User twoDogs = subscribedUser("busca-dos-perros");
        saveLostPostOf(twoDogs, AnimalType.DOG, StatusLostPost.SEARCHING, true);
        saveLostPostOf(twoDogs, AnimalType.DOG, StatusLostPost.SEARCHING, true);
        saveLostPostOf(subscribedUser("busca-gato"), AnimalType.CAT, StatusLostPost.SEARCHING, true);
        LostPost found = new LostPost("Ya aparecio", "Descripción", "cf-img", null, new PhoneNumber("3533", "436249"), true, subscribedUser("encontro-perro"), location(), animal(), null);
        found.transitionTo("FOUND");
        animalPostRepository.save(found);
        saveLostPostOf(subscribedUser("rescata-perro"), AnimalType.DOG, StatusLostPost.TO_RESCUE, false);
        User admin = admin();
        subscribe(admin);
        saveLostPostOf(admin, AnimalType.DOG, StatusLostPost.SEARCHING, true);
    }

    private User subscribedUser(String username) {
        User user = userRepository.save(new User(username, "x",
                new Profile(username + "@mail.com", new PhoneNumber("353", "4014524"), Set.of(Rol.COMMUNITY))));
        subscribe(user);
        return user;
    }

    private void subscribe(User user) {
        PushSubscription subscription = new PushSubscription();
        subscription.setUser(user);
        subscription.setEndpoint("https://push.test/" + user.getUsername());
        subscription.setEndpointHash(Arrays.copyOf(user.getUsername().getBytes(), 32));
        subscription.setP256dh("p256dh");
        subscription.setAuth("auth");
        pushSuscriptionRepository.save(subscription);
    }

    private void saveLostPostOf(User owner, AnimalType type, StatusLostPost status, boolean hasOwner) {
        saveLostPostOf(owner, type, null, status, hasOwner);
    }

    private void saveLostPostOf(User owner, AnimalType type, String color, StatusLostPost status, boolean hasOwner) {
        LostPost post = new LostPost("De " + owner.getUsername(), "Descripción", "cf-img", null, new PhoneNumber("3533", "436249"), hasOwner, owner, location(), animal(type, AnimalSize.MEDIUM, AnimalGender.MALE, null, color), null);
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        animalPostRepository.save(post);
    }

    private void givenPushResponds(int statusCode) throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        given(statusLine.getStatusCode()).willReturn(statusCode);
        given(response.getStatusLine()).willReturn(statusLine);
        given(webPushClient.send(any(), any())).willReturn(response);
    }

    private List<NotificationDelivery> similarLostPetDeliveriesOf(String username) {
        return similarLostPetDeliveriesOf(username, NotificationChannel.PUSH);
    }

    private List<NotificationDelivery> similarLostPetDeliveriesOf(String username, NotificationChannel channel) {
        User recipient = userRepository.findByUsername(username).orElseThrow();
        return notificationDeliveryRepository.findAllByRecipientAndChannelOrderByCreatedAtDesc(recipient, channel)
                .stream()
                .filter(delivery -> delivery.getType() == NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST)
                .toList();
    }

    private ResultActions postAnimalPost(String body) throws Exception {
        return mockMvc.perform(
                post("/animal-posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }

}
