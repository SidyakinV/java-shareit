package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "  JOIN FETCH b.item " +
            "WHERE b.userId = :userId " +
            "  AND (:state is null OR b.state = :state) " +
            "ORDER BY b.start DESC")
    List<Booking> getUserBookings(long userId, BookingState state);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "  JOIN FETCH b.item AS i " +
            "WHERE i.ownerId = :ownerId " +
            "  AND (:state is null OR b.state = :state) " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerBookings(long ownerId, BookingState state);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            " JOIN FETCH b.item AS i " +
            "WHERE b.userId = :userId " +
            "  AND b.state = :state " +
            "  AND i.id = :itemId")
    List<Booking> getUserBookingItems(Long userId, Long itemId, BookingState state);

}
