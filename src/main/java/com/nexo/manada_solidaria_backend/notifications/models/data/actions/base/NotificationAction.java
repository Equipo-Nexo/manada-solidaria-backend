package com.nexo.manada_solidaria_backend.notifications.models.data.actions.base;

public abstract class NotificationAction {
    private String title;

    protected abstract NotificationActionType type();
}
