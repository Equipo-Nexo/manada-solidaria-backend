package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionFormItemResponse {
    private UUID id;
    private String animalName;
    private String animalImageUrl;
    private String description;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static AdoptionFormItemResponse from(AdoptionForm form) {
        return AdoptionFormItemResponse.builder()
                .id(form.getId())
                .animalName(form.getAdoptionPost().getName())
                .animalName(form.getAdoptionPost().getImageUrl())
                .description(form.getDescription())
                .isRead(form.isRead())
                .createdAt(form.getCreatedAt())
                .build();
    }
}