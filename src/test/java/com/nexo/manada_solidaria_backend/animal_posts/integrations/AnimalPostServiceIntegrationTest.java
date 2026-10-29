package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.*;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.*;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AnimalPostServiceIntegrationTest extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private AnimalPostService animalPostService;
    @Autowired
    private AnimalPostRepository animalPostRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("El cron expira las publicaciones abiertas que llevan mas de 120 dias sin actividad")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideExpirationCases")
    void expireInactivePosts(
            String testName,
            AnimalPostFilter postType,
            String status,
            int createdDaysAgo,
            Integer updatedDaysAgo,
            boolean expires
    ) {
        AnimalPost<?, ?> post = buildPost(postType, status);
        post.setCreatedAt(LocalDateTime.now().minusDays(createdDaysAgo));
        post.setUpdatedAt(Optional.ofNullable(updatedDaysAgo).map(LocalDateTime.now()::minusDays).orElse(null));
        UUID seededStatusId = animalPostRepository.save(post).getCurrentStatus().getId();

        animalPostService.expireInactivePosts();

        StatusHistory<?> current = animalPostRepository.findById(post.getId()).orElseThrow().getCurrentStatus();
        assertThat(current.getStatus().name()).isEqualTo(expires ? "EXPIRED" : status);
        assertThat(current.getId().equals(seededStatusId)).isEqualTo(!expires);
    }

    @Test
    @DisplayName("El cron no expira una publicacion vieja que cambio de estado hace poco")
    void expireInactivePosts_transitionCountsAsActivity() {
        AnimalPost<?, ?> post = buildPost(AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT");
        post.setCreatedAt(LocalDateTime.now().minusDays(200));
        post.setUpdatedAt(LocalDateTime.now().minusDays(200));
        post.transitionTo("SEARCHING_ADOPT_AND_TRANSIT");
        animalPostRepository.save(post);

        animalPostService.expireInactivePosts();

        StatusHistory<?> current = animalPostRepository.findById(post.getId()).orElseThrow().getCurrentStatus();
        assertThat(current.getStatus().name()).isEqualTo("SEARCHING_ADOPT_AND_TRANSIT");
    }

    private AnimalPost<?, ?> buildPost(AnimalPostFilter postType, String status) {
        return switch (postType) {
            case LOST -> buildLostPost(StatusLostPost.valueOf(status), true);
            case IN_STREET -> buildLostPost(StatusLostPost.valueOf(status), false);
            case ADOPTION -> buildAdoptionPost(StatusAdoptionPost.valueOf(status));
        };
    }

    private LostPost buildLostPost(StatusLostPost status, boolean hasOwner) {
        LostPost post = new LostPost("Perdido", "Descripción", "cf-img", null, new PhoneNumber("3533", "436249"), hasOwner, admin(), location(), animal(), null);
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        return post;
    }

    private AdoptionPost buildAdoptionPost(StatusAdoptionPost status) {
        AdoptionPost post = new AdoptionPost("Adopcion", "Descripción", "cf-img", null, new PhoneNumber("3533", "436249"), admin(), animal(), location(), false);
        post.setStatusHistory(new ArrayList<>(List.of(new AdoptionPostStatusHistory(status, post))));
        return post;
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private Animal animal() {
        return new Animal(null, null, AnimalSize.MEDIUM, AnimalGender.MALE, AnimalType.DOG);
    }

    private Location location() {
        return new Location("Parque", "Av. Patricias", 100, -34.6, -58.4);
    }
}
