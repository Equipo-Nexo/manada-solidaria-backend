package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
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

    public static AdoptionFormDetailResponse from(AdoptionForm form, List<QuestionCategory> allCategories) {
        Map<UUID, String> answerMap = extractAnswersMap(form);
        Map<UUID, QuestionForm> answeredQuestionsMap = extractAnsweredQuestionsMap(form);

        List<CategoryAnswersResponse> categoriesList = new ArrayList<>();
        categoriesList.addAll(mapRegisteredCategories(allCategories, answerMap));

        mapUncategorizedQuestions(answeredQuestionsMap, answerMap)
                .ifPresent(categoriesList::add);

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

    private static Map<UUID, String> extractAnswersMap(AdoptionForm form) {
        Map<UUID, String> answerMap = new HashMap<>();
        if (form.getAnswers() != null) {
            for (AdoptionFormDetail detail : form.getAnswers()) {
                if (detail.getQuestionForm() != null) {
                    answerMap.put(detail.getQuestionForm().getId(), detail.getAnswer());
                }
            }
        }
        return answerMap;
    }

    private static Map<UUID, QuestionForm> extractAnsweredQuestionsMap(AdoptionForm form) {
        Map<UUID, QuestionForm> questionsMap = new HashMap<>();
        if (form.getAnswers() != null) {
            for (AdoptionFormDetail detail : form.getAnswers()) {
                if (detail.getQuestionForm() != null) {
                    questionsMap.put(detail.getQuestionForm().getId(), detail.getQuestionForm());
                }
            }
        }
        return questionsMap;
    }

    private static List<CategoryAnswersResponse> mapRegisteredCategories(
            List<QuestionCategory> allCategories,
            Map<UUID, String> answerMap
    ) {
        return allCategories.stream()
                .sorted(Comparator.comparing(QuestionCategory::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(category -> toCategoryAnswersResponse(category, answerMap))
                .filter(categoryResponse -> !categoryResponse.questions().isEmpty())
                .toList();
    }

    private static Optional<CategoryAnswersResponse> mapUncategorizedQuestions(
            Map<UUID, QuestionForm> answeredQuestionsMap,
            Map<UUID, String> answerMap
    ) {
        List<QuestionAnswerResponse> uncategorizedQuestions = answeredQuestionsMap.values().stream()
                .filter(q -> q.getCategory() == null)
                .sorted(Comparator.comparing(QuestionForm::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(q -> toQuestionAnswerResponse(q, answerMap))
                .toList();

        if (uncategorizedQuestions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new CategoryAnswersResponse(null, "General", uncategorizedQuestions));
    }

    private static CategoryAnswersResponse toCategoryAnswersResponse(
            QuestionCategory category,
            Map<UUID, String> answerMap
    ) {
        List<QuestionAnswerResponse> questions = category.getQuestions().stream()
                .filter(q -> q.isActive() || answerMap.containsKey(q.getId()))
                .sorted(Comparator.comparing(QuestionForm::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(q -> toQuestionAnswerResponse(q, answerMap))
                .toList();

        return new CategoryAnswersResponse(
                category.getId(),
                category.getName() != null ? category.getName() : "General",
                questions
        );
    }

    private static QuestionAnswerResponse toQuestionAnswerResponse(
            QuestionForm question,
            Map<UUID, String> answerMap
    ) {
        return new QuestionAnswerResponse(
                question.getId(),
                question.getTitle(),
                question.getIconName(),
                answerMap.get(question.getId())
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