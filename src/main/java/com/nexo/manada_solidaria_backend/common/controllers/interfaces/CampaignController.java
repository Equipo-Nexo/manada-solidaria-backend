package com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/campaigns")
public interface CampaignController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CampaignResponse create(
            @Valid @RequestBody CreateCampaignRequest request,
            @AuthenticationPrincipal User owner
    );
}
