package com.nexo.manada_solidaria_backend.vets.data.repositories;

import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface VetInformationRepository extends JpaRepository<VetInformation, UUID> {

    @Query("SELECT v FROM VetInformation v " +
            "LEFT JOIN FETCH v.location l " +
            "LEFT JOIN v.calendar c " +
            "WHERE (:query IS NULL OR TRIM(:query) = '' OR " +
            "       LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "       LOWER(l.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "       LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:openOnly = FALSE OR (" +
            "       c.dayOfWeek = :currentDay AND " +
            "       :currentTime BETWEEN c.openingTime AND c.closingTime" +
            ")) " +
            "ORDER BY " +
            "  CASE WHEN :userLat IS NOT NULL AND :userLng IS NOT NULL AND l.latitude IS NOT NULL AND l.longitude IS NOT NULL THEN " +
            "       ( (l.latitude - :userLat)*(l.latitude - :userLat) + (l.longitude - :userLng)*(l.longitude - :userLng) ) " +
            "  ELSE 0 END ASC, " +
            "  v.name ASC")
    List<VetInformation> searchVets(
            @Param("query") String query,
            @Param("openOnly") boolean openOnly,
            @Param("currentDay") DayOfWeek currentDay,
            @Param("currentTime") LocalTime currentTime,
            @Param("userLat") Double userLat,
            @Param("userLng") Double userLng
    );
}
