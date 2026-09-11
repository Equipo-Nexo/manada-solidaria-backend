package com.nexo.manada_solidaria_backend.guards.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GuardSchedule {

    private static final LocalTime WEEKDAY_CLOSING = LocalTime.of(8, 0);
    private static final LocalTime MONDAY_CLOSING = LocalTime.of(8, 30);
    private static final LocalTime SIESTA_START = LocalTime.of(13, 0);
    private static final LocalTime SIESTA_END = LocalTime.of(16, 0);
    private static final LocalTime NIGHT_START = LocalTime.of(20, 0);
    private static final LocalTime SATURDAY_START = LocalTime.of(13, 0);

    private GuardSchedule() {
    }

    public static boolean isActiveAt(LocalDateTime moment) {
        LocalTime time = moment.toLocalTime();
        return switch (moment.getDayOfWeek()) {
            case SUNDAY -> true;
            case MONDAY -> time.isBefore(MONDAY_CLOSING) || isSiesta(time) || isNight(time);
            case TUESDAY, WEDNESDAY, THURSDAY, FRIDAY ->
                    time.isBefore(WEEKDAY_CLOSING) || isSiesta(time) || isNight(time);
            case SATURDAY -> time.isBefore(WEEKDAY_CLOSING) || !time.isBefore(SATURDAY_START);
        };
    }

    private static boolean isSiesta(LocalTime time) {
        return !time.isBefore(SIESTA_START) && time.isBefore(SIESTA_END);
    }

    private static boolean isNight(LocalTime time) {
        return !time.isBefore(NIGHT_START);
    }
}
