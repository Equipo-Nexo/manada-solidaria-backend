package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AdoptionFormRepository extends JpaRepository<AdoptionForm, UUID> {

    boolean existsByAdoptionPostIdAndApplicantId(UUID adoptionPostId, UUID applicantId);

    List<AdoptionForm> findAllByAdoptionPostId(UUID adoptionPostId);

    List<AdoptionForm> findAllByApplicantId(UUID applicantId);

    @Query("SELECT f FROM AdoptionForm f WHERE f.adoptionPost.owner.id = :ownerId")
    List<AdoptionForm> findAllByPostOwnerId(@Param("ownerId") UUID ownerId);
}
