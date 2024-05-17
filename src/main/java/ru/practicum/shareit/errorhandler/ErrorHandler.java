package ru.practicum.shareit.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.errorhandler.model.ValidationErrorResponse;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedException;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .peek(violation -> log.info("Exception (onConstraintValidationException): " + violation.getMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .peek(violation -> log.info("Exception (onMethodArgumentNotValidException): " + violation.getMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onManualValidationException(final ValidationException e) {
        List<Violation> violations = new ArrayList<>();
        violations.add(e.getViolation());
        log.info("Exception (onManualValidationException): " + e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse onNotFoundException(final NotFoundException e) {
        List<Violation> violations = new ArrayList<>();
        violations.add(e.getViolation());
        log.info("Exception (onNotFoundException): " + e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ValidationErrorResponse onConflictException(final ConflictException e) {
        List<Violation> violations = new ArrayList<>();
        violations.add(e.getViolation());
        log.info("Exception (onConflictException): " + e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> onUnsupportedException(final UnsupportedException e) {
        log.error(e.getMessage());
        return Map.of("error", e.getMessage());
    }

}
