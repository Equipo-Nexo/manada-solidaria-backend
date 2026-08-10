package com.nexo.manada_solidaria_backend.vets.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
