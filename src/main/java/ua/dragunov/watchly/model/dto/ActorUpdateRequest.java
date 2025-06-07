package ua.dragunov.watchly.model.dto;

import java.time.LocalDate;

public record ActorUpdateRequest(
        String firstName,
        String lastName,
        LocalDate birthday,
        String biography,
        String photoUrl
) {}