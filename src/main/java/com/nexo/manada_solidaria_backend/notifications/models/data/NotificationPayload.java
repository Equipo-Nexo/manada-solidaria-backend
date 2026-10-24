package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationPayload {
        private String title;
        private String message;
        private String icon;
        private List<NotificationAction> actions = Collections.emptyList();

    public List<NotificationAction> getActions() {
        return this.actions.stream()
                .sorted(Comparator.comparing(NotificationAction::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}