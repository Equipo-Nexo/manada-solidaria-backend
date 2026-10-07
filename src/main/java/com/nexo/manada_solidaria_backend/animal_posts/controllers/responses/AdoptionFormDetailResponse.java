package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.users.data.models.User;

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

    public static AdoptionFormDetailResponse from(AdoptionForm form) {
        Map<QuestionCategory, List<AdoptionFormDetail>> categoryMap = new LinkedHashMap<>();

        for (AdoptionFormDetail detail : form.getAnswers()) {
            QuestionCategory category = detail.getQuestionForm().getCategory();
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(detail);
        }

        List<CategoryAnswersResponse> categoriesList = categoryMap.entrySet().stream()
                .sorted(Comparator.comparing(
                        entry -> entry.getKey() != null ? entry.getKey().getOrder() : null,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(AdoptionFormDetailResponse::toCategoryAnswersResponse)
                .toList();

        return new AdoptionFormDetailResponse(
                form.getId(),
                form.getAdoptionPost().getId(),
                form.getAdoptionPost().getName(),
                form.getAdoptionPost().getImageUrl(),
                form.getApplicant().getId(),
                buildApplicantFullName(form.getApplicant()),
                form.getDescription(),
                form.getPhoneNumber(),
                form.isRead(),
                form.getCreatedAt(),
                categoriesList
        );
    }

    private static CategoryAnswersResponse toCategoryAnswersResponse(Map.Entry<QuestionCategory, List<AdoptionFormDetail>> entry) {
        QuestionCategory category = entry.getKey();

        List<QuestionAnswerResponse> sortedQuestions = entry.getValue().stream()
                .sorted(Comparator.comparing(
                        detail -> detail.getQuestionForm().getOrder(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(detail -> new QuestionAnswerResponse(
                        detail.getQuestionForm().getId(),
                        detail.getQuestionForm().getTitle(),
                        detail.getQuestionForm().getIconName(),
                        detail.getAnswer()
                ))
                .toList();

        return new CategoryAnswersResponse(
                category != null ? category.getId() : null,
                category != null ? category.getName() : "General",
                sortedQuestions
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