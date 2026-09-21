package com.nexo.manada_solidaria_backend.locations.data.models;

import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String formatted;

    private String district;

    private String street;

    private Integer houseNumber;

    private Double latitude;

    private Double longitude;

    @Id
    private final UUID id = UUID.randomUUID();

    public void update(UpdateLocationRequest request) {
        this.country = request.country();
        this.city = request.city();
        this.formatted = request.formatted();
        this.district = request.district();
        this.street = request.street();
        this.houseNumber = request.houseNumber();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
    }
}