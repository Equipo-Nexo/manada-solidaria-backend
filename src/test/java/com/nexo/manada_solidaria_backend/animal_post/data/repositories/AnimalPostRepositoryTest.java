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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    private LostPostRepository lostPostRepository;

    @Autowired
    private AdoptionPostRepository adoptionPostRepository;

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

    @Test
    @DisplayName("LostPostRepository.findAll devuelve solo lost posts (polimorfismo nativo), ordenados")
    void shouldFindOnlyLostPostsNatively() throws InterruptedException {
        persistLostPost("Perdido 1", StatusLostPost.CREATED);
        Thread.sleep(2);
        persistLostPost("Perdido 2", StatusLostPost.CREATED);
        Thread.sleep(2);
        persistAdoptionPost("Adopción 1", StatusAdoptionPost.CREATED);

        var sorted = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LostPost> lostPage = lostPostRepository.findAll(sorted);

        assertThat(lostPage.getTotalElements()).isEqualTo(2);
        assertThat(lostPage.getContent()).extracting(AnimalPost::getTitle)
                .containsExactly("Perdido 2", "Perdido 1");
    }

    @Test
    @DisplayName("findAllByCurrentStatus de lost discrimina por el estado abierto del historial")
    void shouldFilterLostByCurrentStatus() {
        persistLostPost("En búsqueda", StatusLostPost.SEARCHING);
        persistLostPost("Encontrado", StatusLostPost.FOUND);
        persistLostPost("Recién creado", StatusLostPost.CREATED);
        persistAdoptionPost("Adopción buscando", StatusAdoptionPost.SEARCHING_ADOPT);

        var sorted = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LostPost> searching = lostPostRepository.findAllByCurrentStatus(StatusLostPost.SEARCHING, sorted);
        Page<LostPost> found = lostPostRepository.findAllByCurrentStatus(StatusLostPost.FOUND, sorted);

        assertThat(searching.getContent()).extracting(AnimalPost::getTitle).containsExactly("En búsqueda");
        assertThat(found.getContent()).extracting(AnimalPost::getTitle).containsExactly("Encontrado");
    }

    @Test
    @DisplayName("findAllByCurrentStatus de adoption discrimina por estado y no mezcla con lost")
    void shouldFilterAdoptionByCurrentStatus() {
        persistAdoptionPost("Adoptado", StatusAdoptionPost.ADOPTED);
        persistAdoptionPost("Adopción nueva", StatusAdoptionPost.CREATED);
        persistLostPost("Perdido nuevo", StatusLostPost.CREATED);

        var sorted = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdoptionPost> adopted = adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.ADOPTED, sorted);
        Page<AdoptionPost> created = adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.CREATED, sorted);

        assertThat(adopted.getContent()).extracting(AnimalPost::getTitle).containsExactly("Adoptado");
        // CREATED existe también en StatusLostPost: el repo por subtipo garantiza que solo vengan adopciones.
        assertThat(created.getContent()).extracting(AnimalPost::getTitle).containsExactly("Adopción nueva");
    }

    @Test
    @DisplayName("findAll pagina y ordena por createdAt descendente (más nuevo primero)")
    void shouldPaginateSortedByCreatedAtDesc() throws InterruptedException {
        persistLostPost("Primero", StatusLostPost.CREATED);
        Thread.sleep(2);
        persistAdoptionPost("Segundo", StatusAdoptionPost.CREATED);
        Thread.sleep(2);
        persistLostPost("Tercero", StatusLostPost.CREATED);

        var sorted = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnimalPost> page = animalPostRepository.findAll(sorted);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).extracting(AnimalPost::getTitle)
                .containsExactly("Tercero", "Segundo");
    }

    private void persistLostPost(String title, StatusLostPost currentStatus) {
        var post = new LostPost(title, "Descripción", "cf-img", null, true, null, buildLocation(), buildAnimal());
        var status = new LostPostStatusHistory(currentStatus);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));
        animalPostRepository.save(post);
    }

    private void persistAdoptionPost(String title, StatusAdoptionPost currentStatus) {
        var post = new AdoptionPost(title, "Descripción", "cf-img", null, null, buildAnimal(), buildLocation());
        var status = new AdoptionPostStatusHistory(currentStatus);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));
        animalPostRepository.save(post);
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
