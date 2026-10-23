package com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;

public class CloseAction extends NotificationAction {
    @Override
    protected NotificationActionType type() {
        return NotificationActionType.CLOSE;
    }
}
