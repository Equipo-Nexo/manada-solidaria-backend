package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.users.data.models.User;

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
                buildApplicantFullName(form.getApplicant()),
                form.getDescription(),
                form.getPhoneNumber(),
                form.isRead(),
                form.getCreatedAt(),
                form.getAnswers().stream().map(QuestionFormResponse::from).toList()
        );
    }

    private static String buildApplicantFullName(User user) {
        if (user == null || user.getProfile() == null) {
            return null;
        }

        String name = user.getProfile().getName() != null ? user.getProfile().getName().trim() : "";
        String lastname = user.getProfile().getLastname() != null ? user.getProfile().getLastname().trim() : "";

        String fullName = (name + " " + lastname).trim();
        return fullName.isEmpty() ? null : fullName;
    }
}