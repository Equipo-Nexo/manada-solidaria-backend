package com.nexo.manada_solidaria_backend.locations.data.models;

import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
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
    private String name;
    private String address;
    private Integer number;
    private Double latitude;
    private Double longitude;
    @Id
    private final UUID id = UUID.randomUUID();

    public void update(UpdateLocationRequest request) {
        this.name = request.name();
        this.address = request.address();
        this.number = request.number();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
    }
}