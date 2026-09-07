package com.nexo.manada_solidaria_backend.notifications.components.recipients;

import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;

public interface NotificationRecipient {
    List<User> getRecipients();
}
