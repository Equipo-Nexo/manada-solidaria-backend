package com.nexo.manada_solidaria_backend.notifications.components.recipients.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipient;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetCarriers implements NotificationRecipient {

    private final UserService userService;

    public GetCarriers(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<User> getRecipients() {
        return userService.findAllByRole("CARRIER");
    }
}
