package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;

import java.time.LocalDateTime;
import java.util.*;

public record AdoptionFormDetailResponse(
        UUID id,
        UUID adoptionPostId,
        String animalName,
        String animalImageUrl,
        UUID applicantId,
        String applicantName,
        String description,
        PhoneNumber phoneNumber,
        boolean isRead,
        LocalDateTime createdAt,
        List<CategoryAnswersResponse> categories
) {

    public record CategoryAnswersResponse(
            UUID categoryId,
            String categoryName,
            List<QuestionAnswerResponse> questions
    ) {}

    public record QuestionAnswerResponse(
            UUID questionId,
            String questionTitle,
            String iconName,
            String answer
    ) {}

    public static AdoptionFormDetailResponse from(AdoptionForm form, List<QuestionCategory> allCategories) {
        Map<UUID, String> answerMap = new HashMap<>();
        if (form.getAnswers() != null) {
            for (AdoptionFormDetail detail : form.getAnswers()) {
                if (detail.getQuestionForm() != null) {
                    answerMap.put(detail.getQuestionForm().getId(), detail.getAnswer());
                }
            }
        }

        List<CategoryAnswersResponse> categoriesList = allCategories.stream()
                .sorted(Comparator.comparing(
                        QuestionCategory::getOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(category -> toCategoryAnswersResponse(category, answerMap))
                .toList();

        return new AdoptionFormDetailResponse(
                form.getId(),
                form.getAdoptionPost().getId(),
                form.getAdoptionPost().getName(),
                form.getAdoptionPost().getImageUrl(),
                form.getApplicant().getId(),
                form.getApplicant().getFullNameOrUsername(),
                form.getDescription(),
                form.getPhoneNumber(),
                form.isRead(),
                form.getCreatedAt(),
                categoriesList
        );
    }

    private static CategoryAnswersResponse toCategoryAnswersResponse(
            QuestionCategory category,
            Map<UUID, String> answerMap
    ) {
        List<QuestionAnswerResponse> questions = category.getQuestions().stream()
                .filter(QuestionForm::isActive)
                .sorted(Comparator.comparing(
                        QuestionForm::getOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(q -> new QuestionAnswerResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getIconName(),
                        answerMap.get(q.getId())
                ))
                .toList();

        return new CategoryAnswersResponse(
                category.getId(),
                category.getName() != null ? category.getName() : "General",
                questions
        );
    }
}