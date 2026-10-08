package com.nexo.manada_solidaria_backend.notifications.components.recipients.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.specifications.UserSpecifications;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class InAdoptionAndTransitPetRecipientResolver extends NotificationRecipientResolver {

    public InAdoptionAndTransitPetRecipientResolver(@Lazy UserService userService) {
        super(userService);
    }

    @Override
    public NotificationType supports() {
        return NotificationType.IN_ADOPTION_AND_TRANSIT_PET;
    }

    @Override
    public Specification<User> buildSpecification(NotificationContext context) {
        return UserSpecifications.hasRole(Rol.TRANSITIONAL_HOME)
                .and(UserSpecifications.allExcept(context.postOwnerId()));
    }
}
