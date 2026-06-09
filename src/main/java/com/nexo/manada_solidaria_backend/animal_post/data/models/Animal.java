package com.nexo.manada_solidaria_backend.animal_post.data.models;

import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Animal {
    private String color;
    private String breed;
    private String fur;
    private String age;
    private String description;
    @Enumerated(EnumType.STRING)
    private AnimalSize size;
    @Enumerated(EnumType.STRING)
    private AnimalGender gender;
    @Enumerated(EnumType.STRING)
    private AnimalType type;
    @Id
    private final UUID id = UUID.randomUUID();
}
