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
    public RedirectAction(
            @JsonProperty("title") String title,
            @JsonProperty("order") Byte order,
            @JsonProperty("url") String url
    ) {
        super(title, order);
        this.url = url;
    }

    @Override
    protected NotificationActionType type() {
        return NotificationActionType.REDIRECT;
    }

    @Override
    public NotificationAction createCopyWithReplacedPlaceholders(Map<String, Object> params) {
        return new RedirectAction(
                this.getTitle(),
                this.getOrder(),
                replacePlaceholders(url, params)
        );
    }

}
