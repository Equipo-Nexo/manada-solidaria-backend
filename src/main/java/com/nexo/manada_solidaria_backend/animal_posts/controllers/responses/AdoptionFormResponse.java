package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdoptionFormResponse(
        UUID id,
        UUID adoptionPostId,
        UUID applicantId,
        String applicantName,
        PhoneNumber phoneNumber,
        boolean isRead,
        LocalDateTime createdAt,
        List<QuestionFormResponse> questions
) {
    public record QuestionFormResponse(
            String question,
            String answer
    ) {
        public static QuestionFormResponse from(QuestionForm questionForm) {
            return new QuestionFormResponse(
                    questionForm.getQuestion(),
                    questionForm.getAnswer()
            );
        }
    }

    public static AdoptionFormResponse from(AdoptionForm form) {
        return new AdoptionFormResponse(
                form.getId(),
                form.getAdoptionPost().getId(),
                form.getApplicant().getId(),
                form.getApplicant().getProfile().getName() + " " + form.getApplicant().getProfile().getLastname(),
                form.getPhoneNumber(),
                form.isRead(),
                form.getCreatedAt(),
                form.getQuestions().stream().map(QuestionFormResponse::from).toList()
        );
    }
}
