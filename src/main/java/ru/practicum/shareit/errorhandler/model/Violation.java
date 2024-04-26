package ru.practicum.shareit.errorhandler.model;

import lombok.Data;

@Data
public class Violation {
    private final String name;
    private final String message;
}
