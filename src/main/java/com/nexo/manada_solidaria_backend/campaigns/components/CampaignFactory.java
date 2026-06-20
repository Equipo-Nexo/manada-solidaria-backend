package com.nexo.manada_solidaria_backend.campaigns.components;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaginStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.models.*;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CampaignFactory {

    public Campaign buildCampaign(CreateCampaignRequest request, User owner) {
        Location location = buildLocation(request.location());

        return switch (request.type()) {

            case DONATION ->
                    buildDonationCampaign(
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

    private DonationCampaign buildDonationCampaign(CreateCampaignRequest request, Location location, User owner) {
        DonationCampaign campaign =
                new DonationCampaign(
                        request.title(),
                        request.description(),
                        request.imageId(),
                        null,
                        location,
                        owner,
                        request.amountToBeCollected(),
                        request.campaignEndDate()
                );

        addInitialStatus(campaign);

        return campaign;
    }

    private void addInitialStatus(DonationCampaign campaign) {
        DonationCampaignStatusHistory status = new DonationCampaignStatusHistory(
                DonationCampaignStatus.CREATED,
                campaign
        );
        campaign.getStatusHistory().add(status);
    }

    private NewsCampaign buildNewsCampaign(CreateCampaignRequest request, Location location, User owner) {
        NewsCampaign campaign =
                new NewsCampaign(
                        request.title(),
                        request.description(),
                        request.imageId(),
                        null,
                        location,
                        owner
                );

        NewsCampaignStatusHistory status =
                new NewsCampaignStatusHistory(
                        NewsCampaginStatus.CREATED,
                        campaign
                );
        campaign.getStatusHistory().add(status);

        return campaign;
    }
}
