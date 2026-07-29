package com.nexo.manada_solidaria_backend.vets.controllers.responses;

import com.nexo.manada_solidaria_backend.common.controllers.responses.LocationResponse;
import com.nexo.manada_solidaria_backend.vets.data.models.Schedule;
import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VetInformationResponse(

        UUID id,

        String name,

        String phone,

        String email,

        String profilePictureUrl,

        String vetPageUrl,

        String description,

        LocationResponse location,

        List<ScheduleResponse> calendar

) {

    public VetInformationResponse(VetInformation vet) {
        this(
                vet.getId(),
                vet.getName(),
                vet.getPhone(),
                vet.getEmail(),
                vet.getProfilePictureUrl(),
                vet.getVetPageUrl(),
                vet.getDescription(),
                LocationResponse.from(vet.getLocation()),
                vet.getCalendar()
                        .stream()
                        .map(ScheduleResponse::new)
                        .toList()
        );
    }

    public record ScheduleResponse(DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        public ScheduleResponse(Schedule schedule) {
            this(
                    schedule.getDayOfWeek(),
                    schedule.getOpeningTime(),
                    schedule.getClosingTime()
            );
        }

    }

}