package com.nexo.manada_solidaria_backend.common.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.common.components.ReportMailListener.ReportThresholdReachedEvent;
import com.nexo.manada_solidaria_backend.common.configs.ReportProperties;
import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.common.data.models.ReportPost;
import com.nexo.manada_solidaria_backend.common.data.repositories.ReportPostRepository;
import com.nexo.manada_solidaria_backend.common.services.interfaces.ReportService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final String ALREADY_REPORTED_MESSAGE = "Ya has reportado esta publicación anteriormente";

    private final ReportPostRepository reportPostRepository;
    private final AnimalPostService animalPostService;
    private final CampaignService campaignService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReportProperties reportProperties;

    @Override
    @Transactional
    public void reportPost(CreateReportPostRequest request, User authenticatedUser) {
        UUID postId = request.postId();

        validateNotOwnPost(postId, request.type(), authenticatedUser);
        registerReport(postId, authenticatedUser);
        checkThresholdAndNotify(postId);
    }

    private UUID getOwnerId(UUID postId, com.nexo.manada_solidaria_backend.common.data.enums.ReportableType type) {
        return switch (type) {
            case ANIMAL_POST -> animalPostService.getOwnerId(postId);
            case CAMPAIGN -> campaignService.getOwnerId(postId);
        };
    }

    private void validateNotOwnPost(UUID postId, com.nexo.manada_solidaria_backend.common.data.enums.ReportableType type, User authenticatedUser) {
        UUID ownerId = getOwnerId(postId, type);
        if (ownerId.equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes reportar tu propia publicación");
        }
    }

    private void registerReport(UUID postId, User authenticatedUser) {
        if (reportPostRepository.existsByPostIdAndOwner(postId, authenticatedUser)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_REPORTED_MESSAGE);
        }

        try {
            reportPostRepository.save(new ReportPost(postId, authenticatedUser));
            log.info("Post reported: postId={} by user={}", postId, authenticatedUser.getId());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_REPORTED_MESSAGE);
        }
    }

    private void checkThresholdAndNotify(UUID postId) {
        long totalReports = reportPostRepository.countByPostId(postId);
        if (totalReports == reportProperties.threshold()) {
            applicationEventPublisher.publishEvent(new ReportThresholdReachedEvent(postId));
            log.info("Report threshold reached ({} reports) for post {}. Event published.", totalReports, postId);
        }
    }
}