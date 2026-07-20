package com.nexo.manada_solidaria_backend.campaigns.data.enums;

public enum NewsCampaignCategory {
    VACCINATION("vaccination"),
    CASTRATION("castration"),
    DEWORMING("deworming"),
    OTHER("other");

    private final String value;

    NewsCampaignCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
