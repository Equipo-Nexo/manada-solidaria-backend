package com.nexo.manada_solidaria_backend.campaigns.components;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.models.*;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.stereotype.Component;

@Component
public class CampaignFactory {

    public Campaign buildCampaign(CreateCampaignRequest request, User owner) {
        Location location = buildLocation(request.location());

        return switch (request.type()) {

            case FUNDRAISING ->
                    buildFundraisingCampaign(
                            request,
                            location,
                            owner
                    );

            case NEWS ->
                    buildNewsCampaign(
                            request,
                            location,
                            owner
                    );
        };
    }

    private Location buildLocation(
            CreateCampaignRequest.LocationRequest req
    ) {

        return new Location(
                req.name(),
                req.address(),
                req.number(),
                req.latitude(),
                req.longitude()
        );
    }

    private FundraisingCampaign buildFundraisingCampaign(CreateCampaignRequest request, Location location, User owner) {
        return new FundraisingCampaign(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                location,
                owner,
                request.accountAlias(),
                request.amountToBeCollected(),
                request.campaignEndDate()
        );
    }

    private NewsCampaign buildNewsCampaign(CreateCampaignRequest request, Location location, User owner) {
        return new NewsCampaign(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                location,
                owner
        );
    }
}
