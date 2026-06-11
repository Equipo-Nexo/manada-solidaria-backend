package com.nexo.manada_solidaria_backend.animal_posts.services;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest.AnimalRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.LostPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.implementations.AnimalPostServiceImpl;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AnimalPostServiceTest {

    @Mock
    private AnimalPostRepository animalPostRepository;

    @Mock
    private LostPostRepository lostPostRepository;

    @Mock
    private AdoptionPostRepository adoptionPostRepository;

    private AnimalPostServiceImpl animalPostService;

    @BeforeEach
    void setUp() {
        // Factory real (no tiene dependencias): mantiene la cobertura de cómo se construye cada post.
        animalPostService = new AnimalPostServiceImpl(
                animalPostRepository, lostPostRepository, adoptionPostRepository, new AnimalPostFactory());
    }

    @Test
    @DisplayName("Crea un LostPost con hasOwner, owner nulo, imageId como imageUrl y estado inicial CREATED")
    void shouldBuildLostPostWithInitialStatus() {
        given(animalPostRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        var request = new CreateAnimalPostRequest(
                AnimalPostType.LOST, "Perdí a mi perro", "Se escapo", "cf-image-123", true,
                new AnimalRequest(AnimalType.DOG, AnimalSize.MEDIUM, AnimalGender.MALE,
                        "marrón", "mestizo", "corto", "3 años", "muy amigable"),
                new LocationRequest("Parque", "Av. Patricias", 100, -34.6, -58.4));

        AnimalPostResponse response = animalPostService.create(request, null);

        ArgumentCaptor<AnimalPost> captor = ArgumentCaptor.forClass(AnimalPost.class);
        Mockito.verify(animalPostRepository).save(captor.capture());
        AnimalPost saved = captor.getValue();

        assertThat(saved).isInstanceOf(LostPost.class);
        LostPost lost = (LostPost) saved;
        assertThat(lost.isHasOwner()).isTrue();
        assertThat(lost.getOwner()).isNull();
        assertThat(lost.getImageUrl()).isEqualTo("cf-image-123");
        assertThat(lost.getAnimal().getType()).isEqualTo(AnimalType.DOG);
        assertThat(lost.getStatusHistory()).hasSize(1);

        assertThat(response.type()).isEqualTo(AnimalPostType.LOST);
        assertThat(response.status()).isEqualTo("CREATED");
        assertThat(response.ownerId()).isNull();
    }

    @Test
    @DisplayName("LOST con hasOwner=false construye un post de animal encontrado en la calle")
    void shouldBuildLostPostFoundOnStreet() {
        given(animalPostRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        var request = new CreateAnimalPostRequest(
                AnimalPostType.LOST, "Encontré un gato", "Estaba solo sin collar", "cf-image-found", false,
                new AnimalRequest(AnimalType.CAT, AnimalSize.SMALL, AnimalGender.FEMALE,
                        null, null, null, null, null),
                new LocationRequest("Plaza San Martín", "Rivadavia", 500, -34.6, -58.4));

        var response = animalPostService.create(request, null);

        ArgumentCaptor<AnimalPost> captor = ArgumentCaptor.forClass(AnimalPost.class);
        Mockito.verify(animalPostRepository).save(captor.capture());
        LostPost lost = (LostPost) captor.getValue();

        assertThat(lost.isHasOwner()).isFalse();
        assertThat(lost.getStatusHistory()).hasSize(1);
        assertThat(response.type()).isEqualTo(AnimalPostType.LOST);
    }

    @Test
    @DisplayName("findAll sin type consulta todos los posts forzando orden createdAt DESC")
    void shouldQueryAllSortedByCreatedAtDesc() {
        given(animalPostRepository.findAll(any(Pageable.class)))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(null, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title")));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(animalPostRepository).findAll(captor.capture());
        var order = captor.getValue().getSort().getOrderFor("createdAt");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("findAll con type LOST usa el repo de LostPost (todos los estados)")
    void shouldFilterByLostSubclass() {
        given(lostPostRepository.findAll(any(Pageable.class)))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(AnimalPostFilter.LOST, PageRequest.of(0, 10));

        Mockito.verify(lostPostRepository).findAll(any(Pageable.class));
        Mockito.verify(animalPostRepository, Mockito.never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findAll con type ADOPTION usa el repo de AdoptionPost (todos los estados)")
    void shouldFilterByAdoptionSubclass() {
        given(adoptionPostRepository.findAll(any(Pageable.class)))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(AnimalPostFilter.ADOPTION, PageRequest.of(0, 10));

        Mockito.verify(adoptionPostRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findAll con type SEARCHING filtra LostPost por estado actual SEARCHING")
    void shouldFilterLostByCurrentStatus() {
        given(lostPostRepository.findAllByCurrentStatus(any(), any()))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(AnimalPostFilter.SEARCHING, PageRequest.of(0, 10));

        Mockito.verify(lostPostRepository).findAllByCurrentStatus(eq(StatusLostPost.SEARCHING), any());
        Mockito.verifyNoInteractions(adoptionPostRepository);
    }

    @Test
    @DisplayName("findAll con type ADOPTED filtra AdoptionPost por estado actual ADOPTED")
    void shouldFilterAdoptionByCurrentStatus() {
        given(adoptionPostRepository.findAllByCurrentStatus(any(), any()))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(AnimalPostFilter.ADOPTED, PageRequest.of(0, 10));

        Mockito.verify(adoptionPostRepository).findAllByCurrentStatus(eq(StatusAdoptionPost.ADOPTED), any());
        Mockito.verifyNoInteractions(lostPostRepository);
    }

    @Test
    @DisplayName("findAll con type CREATED filtra solo adopciones (resolución de la colisión de nombres)")
    void shouldMapCreatedFilterToAdoption() {
        given(adoptionPostRepository.findAllByCurrentStatus(any(), any()))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        animalPostService.findAll(AnimalPostFilter.CREATED, PageRequest.of(0, 10));

        Mockito.verify(adoptionPostRepository).findAllByCurrentStatus(eq(StatusAdoptionPost.CREATED), any());
        Mockito.verifyNoInteractions(lostPostRepository);
    }

    @Test
    @DisplayName("findAll mapea cada post con el estado de la entrada más reciente del historial")
    void shouldMapCurrentStatusFromLatestHistoryEntry() throws InterruptedException {
        var post = new LostPost("Perdí a mi perro", "Se escapó", "cf-img-001",
                null, true, null, buildLocation(), buildAnimal());
        var created = new LostPostStatusHistory(StatusLostPost.CREATED, post);
        Thread.sleep(2); // createdAt es final = now(): la pausa garantiza orden estricto entre entradas
        var searching = new LostPostStatusHistory(StatusLostPost.SEARCHING, post);
        post.setStatusHistory(new ArrayList<>(List.of(searching, created)));
        given(animalPostRepository.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        Page<AnimalPostResponse> result = animalPostService.findAll(null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        AnimalPostResponse response = result.getContent().getFirst();
        assertThat(response.status()).isEqualTo("SEARCHING");
        assertThat(response.type()).isEqualTo(AnimalPostType.LOST);
    }

    @Test
    @DisplayName("findAll sin publicaciones devuelve página vacía")
    void shouldReturnEmptyPageWhenNoPosts() {
        given(animalPostRepository.findAll(any(Pageable.class)))
                .willReturn(Page.empty(PageRequest.of(0, 10)));

        Page<AnimalPostResponse> result = animalPostService.findAll(null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    private Animal buildAnimal() {
        var animal = new Animal();
        animal.setType(AnimalType.DOG);
        animal.setSize(AnimalSize.MEDIUM);
        animal.setGender(AnimalGender.MALE);
        return animal;
    }

    private Location buildLocation() {
        return new Location("Parque", "Av. Patricias", 100, -34.6, -58.4);
    }

    @Test
    @DisplayName("Crea un AdoptionPost con hasOwner nulo y estado inicial CREATED")
    void shouldBuildAdoptionPostWithInitialStatus() {
        given(animalPostRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        var request = new CreateAnimalPostRequest(
                AnimalPostType.ADOPTION, "Busco hogar", "Rescatada", "cf-image-456", null,
                new AnimalRequest(AnimalType.CAT, AnimalSize.SMALL, AnimalGender.FEMALE,
                        null, null, null, null, null),
                new LocationRequest("Refugio", "Calle Falsa", 123, -34.5, -58.5));

        AnimalPostResponse response = animalPostService.create(request, null);

        ArgumentCaptor<AnimalPost> captor = ArgumentCaptor.forClass(AnimalPost.class);
        Mockito.verify(animalPostRepository).save(captor.capture());
        AnimalPost saved = captor.getValue();

        assertThat(saved).isInstanceOf(AdoptionPost.class);
        AdoptionPost adoption = (AdoptionPost) saved;
        assertThat(adoption.getStatusHistory()).hasSize(1);

        assertThat(response.type()).isEqualTo(AnimalPostType.ADOPTION);
        assertThat(response.status()).isEqualTo("CREATED");
    }
}
