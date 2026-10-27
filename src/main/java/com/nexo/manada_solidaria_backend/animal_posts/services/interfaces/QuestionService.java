package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    List<QuestionCategoryResponse> getQuestions();

    List<QuestionCategory> getAllCategoriesWithActiveQuestions();

    QuestionForm getQuestionByIdOrThrow(UUID questionId);
}