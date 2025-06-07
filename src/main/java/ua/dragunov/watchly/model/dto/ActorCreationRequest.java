package ua.dragunov.watchly.model.dto;

import java.time.LocalDate;

public record ActorCreationRequest(
        String firstName,
        String lastName,
        LocalDate birthday,
        String biography,
        String photoUrl,
        String externalId,
        long apiSourceId
) {}
