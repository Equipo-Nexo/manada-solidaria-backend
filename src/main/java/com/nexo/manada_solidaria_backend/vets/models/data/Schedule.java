package com.nexo.manada_solidaria_backend.vets.models.data;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class Schedule {
    @ManyToOne
    private VetInformation vet;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
    @Id
    private final UUID id = UUID.randomUUID();

}
