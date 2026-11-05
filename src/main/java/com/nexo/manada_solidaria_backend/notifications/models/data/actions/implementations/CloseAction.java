package com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;

import java.util.Map;

public class CloseAction extends NotificationAction {
    @JsonCreator
    public CloseAction(
            @JsonProperty("title") String title,
            @JsonProperty("order") Byte order
    ) {
        super(title, order);
    }

    @Override
    protected NotificationActionType type() {
        return NotificationActionType.CLOSE;
    }

    @Override
    public NotificationAction createCopyWithReplacedPlaceholders(Map<String, Object> params) {
        return new CloseAction(this.getTitle(), this.getOrder());
    }
}
