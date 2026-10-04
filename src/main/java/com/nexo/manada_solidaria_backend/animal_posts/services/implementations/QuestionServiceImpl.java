package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.QuestionCategoryRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionCategoryRepository questionCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCategoryResponse> getQuestions() {
        return questionCategoryRepository.findAllByOrderByOrderAsc().stream()
                .map(QuestionCategoryResponse::from)
                .toList();
    }
}