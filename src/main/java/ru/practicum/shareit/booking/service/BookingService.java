package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto dto);

    Booking approveBooking(long userId, long itemId, boolean approved);

    Booking getBookingInfo(long userId, long bookingId);

    List<Booking> getUserBookings(long userId, BookingState state, Pageable pageable);

    List<Booking> getOwnerBookings(long ownerId, BookingState state, Pageable pageable);

}
