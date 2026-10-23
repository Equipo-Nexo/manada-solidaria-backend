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

    public NotificationAction(String title) {
        this.title = title;
    }

    @JsonProperty("type")
    protected abstract NotificationActionType type();

    public NotificationAction replaceParams(Map<String, Object> params) {
        return this;
    }
}
