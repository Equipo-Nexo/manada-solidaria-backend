package com.nexo.manada_solidaria_backend.vets.services.implementations;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.data.models.Schedule;
import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import com.nexo.manada_solidaria_backend.vets.data.repositories.VetInformationRepository;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VetInformationServiceImpl implements VetInformationService {

    private final VetInformationRepository repository;

    @Override
    public VetInformationResponse create(CreateVetInformationRequest request) {
        VetInformation vet = buildVetInformation(request);

        repository.save(vet);
        return new VetInformationResponse(vet);
    }

    @Override
    public List<VetInformationResponse> getAll() {
        return repository.findAllByOrderByNameAsc()
                .stream()
                .map(VetInformationResponse::new)
                .toList();
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
                request.phone(),
                request.name()
        );

        List<Schedule> schedules = request.calendar()
                .stream()
                .map(schedule -> buildSchedule(schedule, vet))
                .toList();

        vet.setCalendar(schedules);

        return vet;
    }
    @Override
    public VetInformationResponse getById(UUID vetId) {
        return new VetInformationResponse(getVetInformationOrThrow(vetId));
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