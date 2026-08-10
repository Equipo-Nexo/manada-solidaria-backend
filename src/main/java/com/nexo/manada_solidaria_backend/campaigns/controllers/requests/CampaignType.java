package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

public enum CampaignType {
    DONATION("donation"),
    FUNDRAISING("fundraising"),
    NEWS("news");

    private final String value;

    CampaignType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
