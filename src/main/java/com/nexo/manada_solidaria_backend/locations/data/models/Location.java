package com.nexo.manada_solidaria_backend.locations.data.models;

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

    public void update(String name, String address, Integer number, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
