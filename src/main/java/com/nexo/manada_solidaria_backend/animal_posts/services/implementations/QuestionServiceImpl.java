package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.QuestionCategoryResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.QuestionCategoryRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.QuestionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionCategoryRepository questionCategoryRepository;
    private final QuestionFormRepository questionFormRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCategoryResponse> getQuestions() {
        return questionCategoryRepository.findAllByOrderByOrderAsc().stream()
                .map(QuestionCategoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCategory> getAllCategoriesWithActiveQuestions() {
        return questionCategoryRepository.findAllWithQuestionsOrdered();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionForm getQuestionByIdOrThrow(UUID questionId) {
        return questionFormRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pregunta no encontrada: " + questionId
                ));
    }
}