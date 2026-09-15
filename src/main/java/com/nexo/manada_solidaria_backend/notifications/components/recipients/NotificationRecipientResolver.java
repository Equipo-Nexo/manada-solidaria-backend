package com.nexo.manada_solidaria_backend.notifications.components.recipients;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves the users that should receive a notification based on
 * notification-specific recipient criteria.
 *
 * <p>Each implementation defines the {@link NotificationType} it supports
 * and provides the {@link Specification} used to select the corresponding
 * recipients.</p>
 */
public abstract class NotificationRecipientResolver {

    private final UserService userService;

    protected NotificationRecipientResolver(UserService userService) {
        this.userService = userService;
    }

    /**
     * Resolves the users that should receive the notification.
     *
     * @return the set of users that should be notified
     */
    public Set<User> getRecipients() {
        return new HashSet<>(getUsersBySpecifications());
    }

    private List<User> getUsersBySpecifications() {
        return userService.getUserBySpecifications(buildSpecification());
    }

    /**
     * Returns the notification type supported by this resolver.
     *
     * @return the supported notification type
     */
    protected abstract NotificationType supports();

    /**
     * Builds the specification containing the criteria used to determine
     * which users should receive the notification.
     *
     * @return the specification used to select notification recipients
     */
    protected abstract Specification<User> buildSpecification();
}
