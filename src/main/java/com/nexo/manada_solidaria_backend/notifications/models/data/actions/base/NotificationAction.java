package com.nexo.manada_solidaria_backend.notifications.models.data.actions.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations.CloseAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations.RedirectAction;
import lombok.Getter;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RedirectAction.class, name = "REDIRECT"),
        @JsonSubTypes.Type(value = CloseAction.class, name = "CLOSE")
})
@Getter
public abstract class NotificationAction {
    private String title;
    private Byte order;

    public NotificationAction(String title) {
        this.title = title;
    }

    @JsonProperty("type")
    protected abstract NotificationActionType type();

    protected String replacePlaceholders(
            String text,
            Map<String, Object> params
    ) {
        if (text == null || params == null) {
            return text;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                text = text.replace(
                        "{" + entry.getKey() + "}",
                        entry.getValue().toString()
                );
            }
        }

        return text;
    }

    public NotificationAction replaceParams(Map<String, Object> params) {
        this.title = replacePlaceholders(this.title, params);
        return this;
    }
}
