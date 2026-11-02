package com.nexo.manada_solidaria_backend.common.controllers.implementations;

import com.nexo.manada_solidaria_backend.common.controllers.interfaces.ReportController;
import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.common.services.interfaces.ReportService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

    private final ReportService reportService;

    @Override
    public void reportPost(CreateReportPostRequest request, User authenticatedUser) {
        reportService.reportPost(request, authenticatedUser);
    }
}