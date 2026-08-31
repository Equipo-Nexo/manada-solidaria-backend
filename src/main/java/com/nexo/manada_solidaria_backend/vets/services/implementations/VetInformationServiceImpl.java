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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class VetInformationServiceImpl implements VetInformationService {

    private static final String ZONE_ID = "America/Argentina/Buenos_Aires";
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private final VetInformationRepository repository;

    @Override
    public VetInformationResponse create(CreateVetInformationRequest request) {
        VetInformation vet = buildVetInformation(request);
        VetInformation saved = repository.save(vet);

        log.info("Vet information created: id={} name={}", saved.getId(), saved.getName());

        return new VetInformationResponse(saved);
    }

    @Override
    public List<VetInformationResponse> getAll(String query, Boolean openOnly, Double userLat, Double userLng) {
        log.debug("Listing vets: query={} openOnly={} lat={} lng={}", query, openOnly, userLat, userLng);

        LocalDateTime now = LocalDateTime.now(ZoneId.of(ZONE_ID));
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();
        boolean filterOpen = Boolean.TRUE.equals(openOnly);
        String cleanedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        List<VetInformation> vets = repository.searchVets(cleanedQuery, filterOpen, currentDay, currentTime, userLat, userLng);

        return vets.stream()
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

        log.info("Vet information deleted: id={}", vetId);
    }

    @Override
    public VetInformationResponse update(UUID vetId, UpdateVetInformationRequest request) {
        VetInformation vet = getVetInformationOrThrow(vetId);

        vet.update(request);
        VetInformation updated = repository.save(vet);

        log.info("Vet information updated: id={}", updated.getId());

        return new VetInformationResponse(updated);
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

}