package com.nexo.manada_solidaria_backend.vets.services.implementations;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.data.models.Schedule;
import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import com.nexo.manada_solidaria_backend.vets.data.repositories.VetInformationRepository;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
public class VetInformationServiceImpl implements VetInformationService {

    private static final String ZONE_ID = "America/Argentina/Buenos_Aires";
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private final VetInformationRepository repository;

    @Override
    public VetInformationResponse create(CreateVetInformationRequest request) {
        VetInformation vet = buildVetInformation(request);

        repository.save(vet);
        return new VetInformationResponse(vet);
    }

    @Override
    public List<VetInformationResponse> getAll(String query, Boolean openOnly, Double userLat, Double userLng) {
        String searchQuery = (query != null && !query.trim().isEmpty())
                ? "%" + query.trim() + "%"
                : null;

        List<VetInformation> vets = repository.searchVets(searchQuery);

        return vets.stream()
                .filter(filterByOpenStatus(openOnly))
                .sorted(resolveComparator(userLat, userLng))
                .map(VetInformationResponse::new)
                .toList();
    }

    @Override
    public VetInformationResponse getById(UUID vetId) {
        return new VetInformationResponse(getVetInformationOrThrow(vetId));
    }

    @Override
    public void delete(UUID vetId) {
        VetInformation vet = getVetInformationOrThrow(vetId);
        repository.delete(vet);
    }

    @Override
    public VetInformationResponse update(UUID vetId, UpdateVetInformationRequest request) {
        VetInformation vet = getVetInformationOrThrow(vetId);

        vet.update(request);
        repository.save(vet);
        return new VetInformationResponse(vet);
    }


    private VetInformation buildVetInformation(CreateVetInformationRequest request) {

        Location location = new Location(
                request.location().name(),
                request.location().address(),
                request.location().number(),
                request.location().latitude(),
                request.location().longitude()
        );

        VetInformation vet = new VetInformation(
                location,
                new ArrayList<>(),
                request.description(),
                request.vetPageUrl(),
                request.profilePictureUrl(),
                request.email(),
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                request.name()
        );

        List<Schedule> schedules = request.calendar()
                .stream()
                .map(schedule -> buildSchedule(schedule, vet))
                .toList();

        vet.setCalendar(schedules);

        return vet;
    }

    private Schedule buildSchedule(CreateVetInformationRequest.ScheduleRequest request, VetInformation vet) {
        return new Schedule(vet, request.dayOfWeek(), request.openingTime(), request.closingTime());
    }

    private VetInformation getVetInformationOrThrow(UUID vetId) {
        return repository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La veterinaria no existe"
                ));
    }

    private Predicate<VetInformation> filterByOpenStatus(Boolean openOnly) {
        if (!Boolean.TRUE.equals(openOnly)) {
            return vet -> true;
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of(ZONE_ID));
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        return vet -> isVetOpenAt(vet, currentDay, currentTime);
    }

    private boolean isVetOpenAt(VetInformation vet, DayOfWeek day, LocalTime time) {
        if (vet.getCalendar() == null) {
            return false;
        }

        return vet.getCalendar().stream()
                .anyMatch(schedule -> isScheduleActive(schedule, day, time));
    }

    private boolean isScheduleActive(Schedule schedule, DayOfWeek day, LocalTime time) {
        return schedule.getDayOfWeek() == day &&
                !time.isBefore(schedule.getOpeningTime()) &&
                !time.isAfter(schedule.getClosingTime());
    }

    private Comparator<VetInformation> resolveComparator(Double userLat, Double userLng) {
        return hasCoordinates(userLat, userLng)
                ? Comparator.comparingDouble(vet -> calculateDistanceToUser(vet, userLat, userLng))
                : Comparator.comparing(VetInformation::getName, String.CASE_INSENSITIVE_ORDER);
    }

    private boolean hasCoordinates(Double lat, Double lng) {
        return lat != null && lng != null;
    }

    private double calculateDistanceToUser(VetInformation vet, double userLat, double userLng) {
        if (vet.getLocation() == null ||
                vet.getLocation().getLatitude() == null ||
                vet.getLocation().getLongitude() == null) {
            return Double.MAX_VALUE; // Si no tiene ubicación, va al fondo de la lista
        }

        return haversine(userLat, userLng, vet.getLocation().getLatitude(), vet.getLocation().getLongitude());
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

}