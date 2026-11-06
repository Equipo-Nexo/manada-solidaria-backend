package com.nexo.manada_solidaria_backend.common.services.interfaces;

import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface ReportService {

    void reportPost(CreateReportPostRequest request, User authenticatedUser);
}
