package com.nexo.manada_solidaria_backend.vets.data.repositories;

import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VetInformationRepository extends JpaRepository<VetInformation, UUID> {

    @Query("SELECT DISTINCT v FROM VetInformation v " +
            "LEFT JOIN FETCH v.location l " +
            "LEFT JOIN FETCH v.calendar c " +
            "WHERE (:query IS NULL OR TRIM(:query) = '' OR " +
            "       LOWER(v.name) LIKE LOWER(:query) OR " +
            "       LOWER(l.address) LIKE LOWER(:query) OR " +
            "       LOWER(l.name) LIKE LOWER(:query))")
    List<VetInformation> searchVets(@Param("query") String query);
}
