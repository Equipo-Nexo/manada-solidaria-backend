package com.nexo.manada_solidaria_backend.animal_post.data.repositories;

import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_post.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.globally_quoted_identifiers=true"
})
class AnimalPostRepositoryTest {

    @Autowired
    private AnimalPostRepository animalPostRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Persiste LostPost con Animal, Location y estado CREATED por cascade")
    void shouldPersistLostPost() {
        var animal = buildAnimal();
        var location = buildLocation();
        var post = new LostPost(
                "Perdí a mi perro", "Se escapó en el parque", "cf-img-123",
                null, true, null, location, animal);
        var status = new LostPostStatusHistory(StatusLostPost.CREATED);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));

        LostPost saved = (LostPost) animalPostRepository.save(post);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAnimal().getId()).isNotNull();
        assertThat(saved.getLocation().getId()).isNotNull();
        assertThat(saved.getStatusHistory()).hasSize(1);
        assertThat(saved.isHasOwner()).isTrue();
    }

    @Test
    @DisplayName("Persiste AdoptionPost con Animal, Location y estado CREATED por cascade")
    void shouldPersistAdoptionPost() {
        var animal = buildAnimal();
        var location = buildLocation();
        var post = new AdoptionPost(
                "Gatita en adopción", "Rescatada de la calle", "cf-img-456",
                null, null, animal, location);
        var status = new AdoptionPostStatusHistory(StatusAdoptionPost.CREATED);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));

        AdoptionPost saved = (AdoptionPost) animalPostRepository.save(post);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAnimal().getId()).isNotNull();
        assertThat(saved.getLocation().getId()).isNotNull();
        assertThat(saved.getStatusHistory()).hasSize(1);
    }

    private Animal buildAnimal() {
        var animal = new Animal();
        animal.setType(AnimalType.DOG);
        animal.setSize(AnimalSize.MEDIUM);
        animal.setGender(AnimalGender.MALE);
        return animal;
    }

    private Location buildLocation() {
        return new Location("Parque Centenario", "Av. Patricias Argentinas", 100, -34.606, -58.435);
    }
}
