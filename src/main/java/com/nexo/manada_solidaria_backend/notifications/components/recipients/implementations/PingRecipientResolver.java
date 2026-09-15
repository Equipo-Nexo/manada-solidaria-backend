package com.nexo.manada_solidaria_backend.notifications.components.recipients.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientResolver;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.specifications.UserSpecifications;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PingRecipientResolver extends NotificationRecipientResolver {
    protected PingRecipientResolver(@Lazy UserService userService) {
        super(userService);
    }

    @Override
    protected NotificationType supports() {
        return NotificationType.PING;
    }

    @Override
    protected Specification<User> buildSpecification() {
        return UserSpecifications.all();
    }
}
