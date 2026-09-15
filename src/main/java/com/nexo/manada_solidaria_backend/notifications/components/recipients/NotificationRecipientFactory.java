package com.nexo.manada_solidaria_backend.notifications.components.recipients;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves the appropriate {@link NotificationRecipientResolver}
 * for a given {@link NotificationType}.
 *
 * <p>The selected resolver is responsible for determining which users
 * should receive the notification.</p>
 */
@Component
public class NotificationRecipientFactory {

    private final Map<NotificationType, NotificationRecipientResolver> resolvers;

    public NotificationRecipientFactory(
            List<NotificationRecipientResolver> resolvers
    ) {
        this.resolvers = resolvers.stream()
                .collect(Collectors.toMap(
                        NotificationRecipientResolver::supports,
                        Function.identity()
                ));
    }

    /**
     * Resolves the appropriate {@link NotificationRecipientResolver}
     * for the given notification type.
     *
     * @param notificationType the notification type for which a resolver is required
     * @return the appropriate {@link NotificationRecipientResolver}
     * @throws IllegalArgumentException if no resolver is configured for the given notification type
     */
    public NotificationRecipientResolver resolve(NotificationType notificationType) {
        return Optional.ofNullable(resolvers.get(notificationType))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No recipient resolver configured for notification type [" + notificationType + "]."
                ));
    }
}
