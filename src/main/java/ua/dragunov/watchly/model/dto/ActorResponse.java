package ua.dragunov.watchly.model.dto;

import java.time.LocalDate;

public record ActorResponse(Long id, String firstName, String lastName, LocalDate birthday, String biography, String photoUrl) {


}
