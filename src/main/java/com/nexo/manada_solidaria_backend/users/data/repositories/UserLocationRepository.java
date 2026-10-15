package com.nexo.manada_solidaria_backend.users.data.repositories;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.models.UserLocation;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserLocationRepository extends JpaRepository<UserLocation, UUID> {

    @Query("""
            SELECT avg(recent.latitude) AS latitude, avg(recent.longitude) AS longitude
            FROM (
                SELECT l.latitude AS latitude, l.longitude AS longitude, l.createdAt AS createdAt
                FROM UserLocation l
                WHERE l.user = :user
                ORDER BY l.createdAt DESC
                LIMIT :window
            ) recent
            GROUP BY round(recent.latitude, :zoneDecimals), round(recent.longitude, :zoneDecimals)
            ORDER BY count(*) DESC, max(recent.createdAt) DESC
            """)
    Optional<Coordinates> findMostFrequentZone(
            @Param("user") User user,
            @Param("window") int window,
            @Param("zoneDecimals") int zoneDecimals,
            Limit limit
    );

    interface Coordinates {
        Double getLatitude();

        Double getLongitude();
    }
}
