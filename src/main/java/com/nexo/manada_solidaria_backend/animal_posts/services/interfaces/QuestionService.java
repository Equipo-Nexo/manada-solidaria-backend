package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;

import java.util.List;

public interface QuestionService {

    List<QuestionCategoryResponse> getQuestions();
}