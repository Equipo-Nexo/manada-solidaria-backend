package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AdoptionFormService;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AdoptionFormServiceImpl implements AdoptionFormService {

    private final AdoptionFormRepository adoptionFormRepository;
    private final AnimalPostRepository animalPostRepository;

    @Override
    @Transactional
    public AdoptionFormResponse createForm(CreateAdoptionFormRequest request, User applicant) {
        AdoptionPost post = getAdoptionPostOrThrow(request.adoptionPostId());

        validateNotOwner(post, applicant);
        validateNotAlreadyApplied(post, applicant);
        validatePostIsActive(post);

        AdoptionForm form = buildAdoptionForm(request, applicant, post);

        AdoptionForm saved = adoptionFormRepository.save(form);
        log.info("Adoption form created: id={} for post={} by user={}", saved.getId(), post.getId(), applicant.getId());

        return AdoptionFormResponse.from(saved);
    }

    private void validateNotOwner(AdoptionPost post, User applicant) {
        if (post.getOwner().getId().equals(applicant.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes enviar un formulario de adopción a tu propia publicación"
            );
        }
    }

    private AdoptionForm buildAdoptionForm(CreateAdoptionFormRequest request, User applicant, AdoptionPost post) {
        List<QuestionForm> questions = mapToQuestionForms(request.questions());

        return new AdoptionForm(
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                applicant,
                post,
                questions
        );
    }

    private List<QuestionForm> mapToQuestionForms(List<CreateAdoptionFormRequest.QuestionFormRequest> questionRequests) {
        return questionRequests.stream()
                .map(q -> new QuestionForm(q.question(), q.answer()))
                .toList();
    }

    private AdoptionPost getAdoptionPostOrThrow(UUID adoptionPostId) {
        return animalPostRepository.findById(adoptionPostId)
                .filter(post -> post instanceof AdoptionPost)
                .map(post -> (AdoptionPost) post)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La publicación de adopción no fue encontrada"
                ));
    }

    private void validateNotAlreadyApplied(AdoptionPost post, User applicant) {
        if (adoptionFormRepository.existsByAdoptionPostIdAndApplicantId(post.getId(), applicant.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya has enviado una postulación para esta publicación");
        }
    }

    private void validatePostIsActive(AdoptionPost post) {
        if (post.isAdopted()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden enviar formularios a publicaciones cerradas o adoptadas"
            );
        }
    }
}