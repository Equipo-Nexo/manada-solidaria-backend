package com.nexo.manada_solidaria_backend.notifications.components.recipients.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.specifications.UserSpecifications;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NewAdoptionFormRecipientResolver extends NotificationRecipientResolver {

    public NewAdoptionFormRecipientResolver(@Lazy UserService userService) {
        super(userService);
    }

    @Override
    public NotificationType supports() {
        return NotificationType.NEW_ADOPTION_FORM;
    }

    @Override
    public Specification<User> buildSpecification(NotificationContext context) {
        return UserSpecifications.idIn(java.util.Set.of(context.postOwnerId()));
    }
}