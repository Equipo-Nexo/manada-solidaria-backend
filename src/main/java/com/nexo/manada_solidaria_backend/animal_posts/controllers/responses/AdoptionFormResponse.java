package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdoptionFormResponse(
        UUID id,
        UUID adoptionPostId,
        UUID applicantId,
        String applicantName,
        String description,
        PhoneNumber phoneNumber,
        boolean isRead,
        LocalDateTime createdAt,
        List<QuestionFormResponse> answers
) {
    public record QuestionFormResponse(
            UUID questionId,
            String questionTitle,
            String answer
    ) {
        public static QuestionFormResponse from(AdoptionFormDetail detail) {
            return new QuestionFormResponse(
                    detail.getQuestionForm().getId(),
                    detail.getQuestionForm().getTitle(),
                    detail.getAnswer()
            );
        }
    }

    public static AdoptionFormResponse from(AdoptionForm form) {
        return new AdoptionFormResponse(
                form.getId(),
                form.getAdoptionPost().getId(),
                form.getApplicant().getId(),
                form.getApplicant().getFullNameOrUsername(),
                form.getDescription(),
                form.getPhoneNumber(),
                form.isRead(),
                form.getCreatedAt(),
                form.getAnswers().stream().map(QuestionFormResponse::from).toList()
        );
    }
}