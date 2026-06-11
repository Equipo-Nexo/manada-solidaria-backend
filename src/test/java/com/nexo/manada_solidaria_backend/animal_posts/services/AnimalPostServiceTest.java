package com.nexo.manada_solidaria_backend.animal_posts.services;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest.AnimalRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.implementations.AnimalPostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AnimalPostServiceTest {

    @Mock
    private AnimalPostRepository animalPostRepository;

    private AnimalPostServiceImpl animalPostService;

    @BeforeEach
    void setUp() {
        // Factory real (no tiene dependencias): mantiene la cobertura de cómo se construye cada post.
        animalPostService = new AnimalPostServiceImpl(animalPostRepository, new AnimalPostFactory());
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
