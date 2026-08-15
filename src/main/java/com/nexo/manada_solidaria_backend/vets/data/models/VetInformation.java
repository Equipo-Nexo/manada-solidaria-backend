package com.nexo.manada_solidaria_backend.vets.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetInformation {
    private String name;
    private String areaCode;
    private String phone;
    private String email;
    private String profilePictureUrl;
    private String vetPageUrl;
    private String description;
    @OneToMany(mappedBy = "vet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> calendar;
    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;
    @Id
    private UUID id = UUID.randomUUID();

    public VetInformation(Location location, List<Schedule> calendar, String description, String vetPageUrl, String profilePictureUrl, String email, String areaCode, String phone, String name) {
        this.areaCode = areaCode;
        this.location = location;
        this.calendar = calendar;
        this.description = description;
        this.vetPageUrl = vetPageUrl;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phone = phone;
        this.name = name;
    }

    public void update(UpdateVetInformationRequest request) {
        this.name = request.name();
        this.areaCode = request.areaCode();
        this.phone = request.phone();
        this.email = request.email();
        this.profilePictureUrl = request.profilePictureUrl();
        this.vetPageUrl = request.vetPageUrl();
        this.description = request.description();

        this.location.update(request.location());

        updateCalendar(request);
    }

    private void updateCalendar(UpdateVetInformationRequest request) {
        this.calendar.clear();

        List<Schedule> schedules = request.calendar()
                .stream()
                .map(schedule -> new Schedule(
                        this,
                        schedule.dayOfWeek(),
                        schedule.openingTime(),
                        schedule.closingTime()
                ))
                .toList();

        this.calendar.addAll(schedules);
    }
}
