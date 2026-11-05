package com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;
import lombok.Getter;

import java.util.HashMap;
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
            @JsonProperty("order") Byte order,
            @JsonProperty("url") String url,
            @JsonProperty("httpMethod") String httpMethod,
            @JsonProperty("headers") Map<String, String> headers
    ) {
        super(title, order);
        this.url = url;
        this.httpMethod = httpMethod;
        this.headers = Optional.ofNullable(headers).orElse(emptyMap());
    }

    @Override
    protected NotificationActionType type() {
        return NotificationActionType.REQUEST;
    }

    @Override
    public NotificationAction createCopyWithReplacedPlaceholders(Map<String, Object> params) {
        return new RequestAction(
                replacePlaceholders(getTitle(), params),
                this.getOrder(),
                replacePlaceholders(url, params),
                httpMethod,
                replaceHeadersPlaceholders(params)
        );
    }

    private Map<String, String> replaceHeadersPlaceholders(Map<String, Object> params) {
        Map<String, String> replacedHeaders = new HashMap<>();
        this.headers.forEach((key, value) -> replacedHeaders.put(key, replacePlaceholders(value, params)));
        return replacedHeaders;
    }
}
