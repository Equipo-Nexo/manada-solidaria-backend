package com.nexo.manada_solidaria_backend.events.listeners;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseEventNotificationListener<T> {

    protected final NotificationService notificationService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onEvent(T event) {
        if (!shouldNotify(event)) {
            return;
        }

        NotificationType type = getNotificationType();
        Map<String, Object> params = getParams(event);
        NotificationContext context = getContext(event);

        log.debug("Sending notification [{}]", type);
        notificationService.notify(type, params, context);
    }

    protected abstract boolean shouldNotify(T event);

    protected abstract NotificationType getNotificationType();

    protected abstract Map<String, Object> getParams(T event);

    protected NotificationContext getContext(T event) {
        return null;
    }
}
