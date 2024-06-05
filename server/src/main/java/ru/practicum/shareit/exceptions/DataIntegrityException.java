package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class DataIntegrityException extends DataIntegrityViolationException {
    public DataIntegrityException(final String message) {
        super(message);
    }
}
