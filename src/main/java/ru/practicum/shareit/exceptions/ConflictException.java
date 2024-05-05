package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.errorhandler.model.Violation;

@RequiredArgsConstructor
@Getter
public class ConflictException extends RuntimeException {
    private final Violation violation;
}
