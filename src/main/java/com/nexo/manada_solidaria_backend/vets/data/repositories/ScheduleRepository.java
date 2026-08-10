package com.nexo.manada_solidaria_backend.vets.data.repositories;

import com.nexo.manada_solidaria_backend.vets.data.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
}
