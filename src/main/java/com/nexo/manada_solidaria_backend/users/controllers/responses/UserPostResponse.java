package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "La misma card que devuelve la home, más postType.")
public record UserPostResponse(
        @Schema(description = "animal, campaign o fundraising", example = "animal")
        String postType,
        @Schema(hidden = true) @JsonUnwrapped Object post,
        @Schema(hidden = true) @JsonIgnore LocalDateTime createdAt
) {

    public static final String ANIMAL = "animal";
    public static final String CAMPAIGN = "campaign";
    public static final String FUNDRAISING = "fundraising";

    public static UserPostResponse animal(AnimalPostResponse post) {
        return new UserPostResponse(ANIMAL, post, post.createdAt());
    }

    public static UserPostResponse campaign(CampaignResponse post) {
        return new UserPostResponse(CAMPAIGN, post, post.createdAt());
    }

    public static UserPostResponse fundraising(CampaignResponse post) {
        return new UserPostResponse(FUNDRAISING, post, post.createdAt());
    }
}
