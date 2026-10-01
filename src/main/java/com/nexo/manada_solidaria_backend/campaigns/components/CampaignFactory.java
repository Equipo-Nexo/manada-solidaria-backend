package com.nexo.manada_solidaria_backend.campaigns.components;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.models.*;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.locations.controllers.requests.LocationRequest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.locations.mappers.LocationMapper;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignFactory {

    private final LocationMapper locationMapper;

    public CampaignFactory(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public Campaign buildCampaign(CreateCampaignRequest request, User owner) {
        Location location = locationMapper.toEntity(request.location());

        return switch (request.type()) {
            case FUNDRAISING -> buildFundraisingCampaign(request, location, owner);
            case NEWS -> buildNewsCampaign(request, location, owner);
            case DONATION -> buildDonationCampaign(request, location, owner);
        };
    }

    private FundraisingCampaign buildFundraisingCampaign(CreateCampaignRequest request, Location location, User owner) {
        return new FundraisingCampaign(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                PhoneNumberRequest.toDomain(request.phoneNumber()),
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
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                location,
                owner,
                request.newsStartDateTime(),
                request.newsEndDateTime(),
                request.category()
        );
    }

    private DonationCampaign buildDonationCampaign(CreateCampaignRequest request, Location location, User owner) {
        DonationCampaign campaign = new DonationCampaign(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                location,
                owner,
                request.campaignEndDate()
        );

        addItemsToCampaign(campaign, request.items());

        return campaign;
    }

    private void addItemsToCampaign(DonationCampaign campaign, List<CreateCampaignRequest.DonationItemRequest> itemRequests) {
        if (itemRequests == null) {
            return;
        }

        itemRequests.forEach(itemReq ->
                campaign.addItem(new DonationItem(itemReq.name(), itemReq.category()))
        );
    }
}
