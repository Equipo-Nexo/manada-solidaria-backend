package com.nexo.manada_solidaria_backend.common.utils;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;

public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private GeoUtils() {}

    public static Double calculateDistanceInKm(Double userLat, Double userLng, Location location) {
        if (!hasValidCoordinates(userLat, userLng, location)) {
            return null;
        }

        double vetLat = location.getLatitude();
        double vetLng = location.getLongitude();

        double dLat = Math.toRadians(vetLat - userLat);
        double dLng = Math.toRadians(vetLng - userLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(vetLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return Math.round(distance * 100.0) / 100.0;
    }

    private static boolean hasValidCoordinates(Double userLat, Double userLng, Location location) {
        return userLat != null
                && userLng != null
                && location != null
                && location.getLatitude() != null
                && location.getLongitude() != null;
    }
}
