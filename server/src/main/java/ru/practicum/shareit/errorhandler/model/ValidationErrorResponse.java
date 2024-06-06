package ru.practicum.shareit.errorhandler.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationErrorResponse {
    private final List<Violation> violations;
}
