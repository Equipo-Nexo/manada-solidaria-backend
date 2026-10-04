package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormDetailResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.FormFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.QuestionFormRepository;
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
    private final QuestionFormRepository questionFormRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionFormResponse> getFormsByPostId(UUID animalPostId) {
        AdoptionPost post = getAdoptionPostOrThrow(animalPostId);

        List<AdoptionForm> forms = adoptionFormRepository.findAllByAdoptionPostId(post.getId());

        return mapToAdoptionFormResponses(forms);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionFormResponse> getFormsByUser(UUID userId, FormFilter filter) {
        List<AdoptionForm> forms = fetchFormsByFilter(userId, filter);
        return mapToAdoptionFormResponses(forms);
    }

    @Override
    @Transactional(readOnly = true)
    public AdoptionFormDetailResponse getFormById(UUID adoptionFormId, User authenticatedUser) {
        AdoptionForm form = getAdoptionFormOrThrow(adoptionFormId);

        validateCanAccessForm(form, authenticatedUser);

        return AdoptionFormDetailResponse.from(form);
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
        AdoptionForm form = new AdoptionForm(
                request.description(),
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                applicant,
                post
        );

        request.answers().forEach(aReq -> form.addAnswer(buildFormDetail(aReq, form)));

        return form;
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

    private List<AdoptionFormResponse> mapToAdoptionFormResponses(List<AdoptionForm> forms) {
        return forms.stream()
                .map(AdoptionFormResponse::from)
                .toList();
    }

    private List<AdoptionForm> fetchFormsByFilter(UUID userId, FormFilter filter) {
        return switch (filter) {
            case OWNER -> adoptionFormRepository.findAllByApplicantId(userId);
            case REVIEWER -> adoptionFormRepository.findAllByPostOwnerId(userId);
        };
    }

    private AdoptionFormDetail buildFormDetail(CreateAdoptionFormRequest.AnswerFormRequest aReq, AdoptionForm form) {
        QuestionForm questionForm = questionFormRepository.findById(aReq.questionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pregunta no encontrada: " + aReq.questionId()
                ));

        return new AdoptionFormDetail(aReq.answer(), form, questionForm);
    }

    private AdoptionForm getAdoptionFormOrThrow(UUID adoptionFormId) {
        return adoptionFormRepository.findById(adoptionFormId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El formulario de adopción no fue encontrado"
                ));
    }

    private void validateCanAccessForm(AdoptionForm form, User user) {
        boolean isApplicant = form.getApplicant().getId().equals(user.getId());
        boolean isPostOwner = form.getAdoptionPost().getOwner().getId().equals(user.getId());

        if (!isApplicant && !isPostOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para consultar este formulario de adopción"
            );
        }
    }
}