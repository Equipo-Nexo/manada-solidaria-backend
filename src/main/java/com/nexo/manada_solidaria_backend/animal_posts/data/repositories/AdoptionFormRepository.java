package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AdoptionFormRepository extends JpaRepository<AdoptionForm, UUID> {

    boolean existsByAdoptionPostIdAndApplicantId(UUID adoptionPostId, UUID applicantId);

    List<AdoptionForm> findAllByAdoptionPostId(UUID adoptionPostId);

    @EntityGraph(attributePaths = {"applicant", "applicant.profile", "adoptionPost"})
    @Query("SELECT f FROM AdoptionForm f WHERE f.applicant.id = :applicantId ORDER BY f.isRead ASC, f.createdAt DESC")
    List<AdoptionForm> findAllByApplicantId(@Param("applicantId") UUID applicantId);

    @EntityGraph(attributePaths = {"applicant", "applicant.profile", "adoptionPost"})
    @Query("SELECT f FROM AdoptionForm f WHERE f.adoptionPost.owner.id = :ownerId ORDER BY f.isRead ASC, f.createdAt DESC")
    List<AdoptionForm> findAllByPostOwnerId(@Param("ownerId") UUID ownerId);

    @Query("""
        SELECT DISTINCT f FROM AdoptionForm f
        LEFT JOIN FETCH f.answers a
        LEFT JOIN FETCH a.questionForm q
        LEFT JOIN FETCH q.category c
        WHERE f.id = :id
        ORDER BY c.order ASC NULLS LAST, q.order ASC NULLS LAST
    """)
    Optional<AdoptionForm> findByIdWithDetailsOrdered(@Param("id") UUID id);

    @Query("SELECT f.adoptionPost.id FROM AdoptionForm f WHERE f.applicant = :applicant AND f.adoptionPost.id IN :postIds")
    Set<UUID> findPostIdsWithFormByApplicantAndPostIds(@Param("applicant") User applicant, @Param("postIds") List<UUID> postIds);
}