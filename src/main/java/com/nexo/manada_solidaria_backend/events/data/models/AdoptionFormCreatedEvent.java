package com.nexo.manada_solidaria_backend.events.data.models;

import java.util.UUID;

public record AdoptionFormCreatedEvent(
        UUID adoptionFormId,
        UUID postOwnerId,
        String applicantName
) {}