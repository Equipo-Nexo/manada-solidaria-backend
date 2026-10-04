package com.nexo.manada_solidaria_backend.animal_posts.controllers.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces.QuestionController;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class QuestionControllerImpl implements QuestionController {

    private final QuestionService questionService;

    @Override
    public List<QuestionCategoryResponse> getQuestions() {
        return questionService.getQuestions();
    }
}