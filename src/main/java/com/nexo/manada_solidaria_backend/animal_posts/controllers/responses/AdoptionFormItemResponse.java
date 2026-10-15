package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.users.data.models.User;
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
    private String applicantName;
    private String description;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static AdoptionFormItemResponse from(AdoptionForm form) {
        return AdoptionFormItemResponse.builder()
                .id(form.getId())
                .animalName(form.getAdoptionPost().getName())
                .animalImageUrl(form.getAdoptionPost().getImageUrl())
                .applicantName(getApplicantName(form.getApplicant()))
                .description(form.getDescription())
                .isRead(form.isRead())
                .createdAt(form.getCreatedAt())
                .build();
    }

    private static String getApplicantName(User applicant) {
        return applicant != null ? applicant.getFullNameOrUsername() : null;
    }
}