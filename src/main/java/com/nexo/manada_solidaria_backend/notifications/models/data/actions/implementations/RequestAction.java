package com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;

@Getter
public class RequestAction extends NotificationAction {

    private String url;
    private String httpMethod;
    private Map<String, String> headers;

    @JsonCreator
    public RequestAction(
            @JsonProperty("title") String title,
            @JsonProperty("url") String url,
            @JsonProperty("httpMethod") String httpMethod,
            @JsonProperty("headers") Map<String, String> headers
    ) {
        super(title);
        this.url = url;
        this.httpMethod = httpMethod;
        this.headers = Optional.of(headers).orElse(emptyMap());
    }

    @Override
    protected NotificationActionType type() {
        return NotificationActionType.REQUEST;
    }

    @Override
    public RequestAction replaceParams(Map<String, Object> params) {
        super.replaceParams(params);
        this.url = replacePlaceholders(this.url, params);
        this.headers.forEach((key, value) -> this.headers.put(key, replacePlaceholders(value, params)));
        return this;
    }
}
