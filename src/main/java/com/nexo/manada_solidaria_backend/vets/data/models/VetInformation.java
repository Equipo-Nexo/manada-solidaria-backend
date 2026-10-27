package com.nexo.manada_solidaria_backend.vets.data.models;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VetInformation {
    private String name;
    @Embedded
    private PhoneNumber phoneNumber;
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

    public VetInformation(Location location, List<Schedule> calendar, String description, String vetPageUrl, String profilePictureUrl, String email, PhoneNumber phoneNumber, String name) {
        this.location = location;
        this.calendar = calendar;
        this.description = description;
        this.vetPageUrl = vetPageUrl;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public void update(UpdateVetInformationRequest request) {
        this.name = request.name();
        this.phoneNumber = PhoneNumberRequest.toDomain(request.phoneNumber());
        this.email = request.email();
        this.profilePictureUrl = request.profilePictureUrl();
        this.vetPageUrl = request.vetPageUrl();
        this.description = request.description();

        this.location.update(request.location());

        updateCalendar(request);
    }

    public boolean isOpen(LocalDateTime dateTime) {
        if (hasNoSchedules()) {
            return false;
        }

        return this.calendar.stream()
                .anyMatch(schedule -> isScheduleOpen(schedule, dateTime.getDayOfWeek(), dateTime.toLocalTime()));
    }

    private boolean hasNoSchedules() {
        return this.calendar == null || this.calendar.isEmpty();
    }

    private boolean isScheduleOpen(Schedule schedule, DayOfWeek day, LocalTime time) {
        return isSameDay(schedule, day) && isTimeWithinRange(schedule, time);
    }

    private boolean isSameDay(Schedule schedule, DayOfWeek day) {
        return schedule.getDayOfWeek() == day;
    }

    private boolean isTimeWithinRange(Schedule schedule, LocalTime time) {
        return !time.isBefore(schedule.getOpeningTime())
                && !time.isAfter(schedule.getClosingTime());
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
