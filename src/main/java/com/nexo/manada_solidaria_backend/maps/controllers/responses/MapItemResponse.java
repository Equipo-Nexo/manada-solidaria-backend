package com.nexo.manada_solidaria_backend.maps.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.common.controllers.responses.LocationResponse;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;

import java.time.DayOfWeek;
import java.util.Comparator;
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
        DescriptionLine secondLineDescription,
        Double longitude,
        Double latitude,
        String location
) {

    public MapItemResponse(UUID id, String imageUrl, String name, String status, DescriptionLine firstLineDescription, DescriptionLine secondLineDescription, LocationResponse location) {
        this(id, imageUrl, name, status, firstLineDescription, secondLineDescription, location.longitude(), location.latitude(), describe(location));
    }

    public static MapItemResponse from(AnimalPostResponse post, long daysSincePublished) {
        return new MapItemResponse(
                post.id(),
                post.imageUrl(),
                post.name(),
                post.status(),
                DescriptionLine.clock(String.valueOf(daysSincePublished)),
                DescriptionLine.locationPin(describe(post.location())),
                post.location()
        );
    }

    public static MapItemResponse from(VetInformationResponse vet, DayOfWeek today) {
        return new MapItemResponse(
                vet.id(),
                vet.profilePictureUrl(),
                vet.name(),
                vet.isOpen() ? "OPEN" : "CLOSED",
                DescriptionLine.phone(vet.phoneNumber().areaCode() + "-" + vet.phoneNumber().number()),
                DescriptionLine.clock(describeSchedule(vet, today)),
                vet.location()
        );
    }

    private static String describe(LocationResponse location) {
        String address = joinPresent(" ", location.address(), Objects.toString(location.number(), null));
        String description = joinPresent(", ", address, location.name());
        return description.isEmpty() ? null : description;
    }

    private static String describeSchedule(VetInformationResponse vet, DayOfWeek today) {
        String schedule = vet.calendar().stream()
                .filter(shift -> shift.dayOfWeek() == today)
                .sorted(Comparator.comparing(VetInformationResponse.ScheduleResponse::openingTime))
                .map(shift -> shift.openingTime() + " - " + shift.closingTime())
                .collect(Collectors.joining(" | "));
        return schedule.isEmpty() ? null : schedule;
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

        public static DescriptionLine locationPin(String text) {
            return new DescriptionLine("LocationPin", text);
        }
    }
}
