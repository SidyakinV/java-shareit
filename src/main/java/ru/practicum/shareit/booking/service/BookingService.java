package ru.practicum.shareit.booking.service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking);

    Booking approveBooking(long userId, long itemId, boolean approved);

    Booking getBookingInfo(long userId, long bookingId);

    List<Booking> getUserBookings(long userId, BookingState state);

    List<Booking> getOwnerBookings(long ownerId, BookingState state);

    List<Booking> getAllTest();

}
