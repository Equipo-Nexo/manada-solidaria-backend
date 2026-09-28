package com.nexo.manada_solidaria_backend.common.controllers.responses;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record MapItemResponse(
        UUID id,
        String imageUrl,
        String name,
        String status,
        DescriptionLine firstLineDescription,
        Double longitude,
        Double latitude,
        String location
) {

    public MapItemResponse(UUID id, String imageUrl, String name, String status, DescriptionLine firstLineDescription, Location location) {
        this(id, imageUrl, name, status, firstLineDescription, location.getLongitude(), location.getLatitude(), describe(location));
    }

    private static String describe(Location location) {
        String address = joinPresent(" ", location.getAddress(), Objects.toString(location.getNumber(), null));
        String description = joinPresent(", ", address, location.getName());
        return description.isEmpty() ? null : description;
    }

    private static String joinPresent(String separator, String... parts) {
        return Stream.of(parts)
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(separator));
    }

    public record DescriptionLine(String iconName, String text) {

        public static DescriptionLine clock(String text) {
            return new DescriptionLine("Clock", text);
        }

        public static DescriptionLine phone(String text) {
            return new DescriptionLine("Phone", text);
        }
    }
}
