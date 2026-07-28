package com.nexo.manada_solidaria_backend.vets.data.repositories;

import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VetInformationRepository extends JpaRepository<VetInformation, UUID> {
}
