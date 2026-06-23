package com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<CampaignResponse> getAllCampaigns(
            @RequestParam(value = "type", required = false) String type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
