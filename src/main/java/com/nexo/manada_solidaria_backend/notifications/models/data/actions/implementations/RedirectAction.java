package com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class RedirectAction extends NotificationAction {
    private String url;

    @JsonCreator
    public RedirectAction(@JsonProperty("url") String url) {
        super("Ver publicación");
        this.url = url;
    }

    @Override
    protected NotificationActionType type() {
        return NotificationActionType.REDIRECT;
    }

    @Override
    public RedirectAction replaceParams(Map<String, Object> params) {
        super.replaceParams(params);
        this.url = replacePlaceholders(this.url, params);
        return this;
    }
}
