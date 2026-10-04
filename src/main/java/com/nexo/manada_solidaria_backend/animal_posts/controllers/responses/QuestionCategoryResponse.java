package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.QuestionType;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionFormDetail;

import java.util.List;
import java.util.UUID;

public record QuestionCategoryResponse(
        UUID id,
        String category,
        String description,
        List<QuestionFormResponse> questions
) {

    public record QuestionFormResponse(
            UUID id,
            QuestionType type,
            String iconName,
            String title,

            @JsonProperty("placeHolder")
            String placeholder,

            List<QuestionDetailResponse> details
    ) {
        public static QuestionFormResponse from(QuestionForm question) {
            return new QuestionFormResponse(
                    question.getId(),
                    question.getQuestionType(),
                    question.getIconName(),
                    question.getTitle(),
                    question.getPlaceholder(),
                    question.getDetails().stream()
                            .map(QuestionDetailResponse::from)
                            .toList()
            );
        }
    }

    public record QuestionDetailResponse(
            String description
    ) {
        public static QuestionDetailResponse from(QuestionFormDetail detail) {
            return new QuestionDetailResponse(detail.getDescription());
        }
    }

    public static QuestionCategoryResponse from(QuestionCategory category) {
        return new QuestionCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getQuestions().stream()
                        .filter(QuestionForm::isActive)
                        .map(QuestionFormResponse::from)
                        .toList()
        );
    }
}