package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.UnsupportedException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, APPROVED;

    public static BookingState stringToBookingState(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new UnsupportedException(String.format("Unknown state: %s", value));
        }
    }
}
