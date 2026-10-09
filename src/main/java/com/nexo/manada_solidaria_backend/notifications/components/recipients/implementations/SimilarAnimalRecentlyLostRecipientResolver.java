package com.nexo.manada_solidaria_backend.notifications.components.recipients.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
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
public class SimilarAnimalRecentlyLostRecipientResolver extends NotificationRecipientResolver {

    private final AnimalPostService animalPostService;

    public SimilarAnimalRecentlyLostRecipientResolver(@Lazy UserService userService, @Lazy AnimalPostService animalPostService) {
        super(userService);
        this.animalPostService = animalPostService;
    }

    @Override
    public NotificationType supports() {
        return NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST;
    }

    @Override
    public Specification<User> buildSpecification(NotificationContext context) {
        return UserSpecifications.idIn(animalPostService.getSearchingOwnerIds(context.animal().type(), context.animal().color()))
                .and(UserSpecifications.allExcept(context.postOwnerId()));
    }
}
