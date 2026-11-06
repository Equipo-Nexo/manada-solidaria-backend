package com.nexo.manada_solidaria_backend.users.data.repositories;

import com.nexo.manada_solidaria_backend.users.data.models.TermsAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TermsAcceptanceRepository extends JpaRepository<TermsAcceptance, UUID> {
}
