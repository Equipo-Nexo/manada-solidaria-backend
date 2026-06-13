package com.nexo.manada_solidaria_backend.vets.models.data;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.UUID;

@Entity
public class VetInformation {
    private String name;
    private String phone;
    private String email;
    private String profilePictureUrl;
    private String vetPageUrl;
    private String description;
    @OneToMany(mappedBy = "vet")
    private List<Schedule> caledar;
    @ManyToOne
    private final Location location;
    @Id
    private UUID id = UUID.randomUUID();

    public VetInformation(Location location, List<Schedule> caledar, String description, String vetPageUrl, String profilePictureUrl, String email, String phone, String name) {
        this.location = location;
        this.caledar = caledar;
        this.description = description;
        this.vetPageUrl = vetPageUrl;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phone = phone;
        this.name = name;
    }
}
