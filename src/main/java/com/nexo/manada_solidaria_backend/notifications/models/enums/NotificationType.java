package com.nexo.manada_solidaria_backend.notifications.models.enums;

import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;

import java.util.Set;

public enum NotificationType {

    PING(
            NotificationChannel.PUSH
    ),

    LOST_PET(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    FOUND_PET(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    IN_ADOPTION_PET(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    IN_ADOPTION_AND_TRANSIT_PET(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    NEW_VACCINATION_CAMPAIGN(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    NEW_DONATION_CAMPAIGN(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    NEW_CASTRATION_CAMPAIGN(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    ),

    NEW_CARRIAGE_REQUEST(
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
    );

    private final Set<NotificationChannel> channels;

    NotificationType(NotificationChannel... channels) {
        this.channels = Set.of(channels);
    }

    public Set<NotificationChannel> getChannels() {
        return channels;
    }
}