package com.nexo.manada_solidaria_backend.notifications.models.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotificationPayload {
        private String title;
        private String message;
        private String icon;
}