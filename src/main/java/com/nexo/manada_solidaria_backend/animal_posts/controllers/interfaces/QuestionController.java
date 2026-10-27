package com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/questions")
public interface QuestionController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<QuestionCategoryResponse> getQuestions();
}