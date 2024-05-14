package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OwnerBookingInfo;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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

    @Query(value =
            "SELECT b.* " +
            "FROM bookings AS b " +
            "  JOIN items AS i ON b.item_id = i.id " +
            "WHERE b.item_id = :itemId " +
            "  AND i.owner_id = :ownerId " +
            "  AND b.start_time <= now() " +
            "  AND b.state = 'APPROVED' " +
            "ORDER BY b.id DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Booking getLastBooking(Long itemId, Long ownerId);

    @Query(value =
            "SELECT b.* " +
            "FROM bookings AS b " +
            "  JOIN items AS i ON b.item_id = i.id " +
            "WHERE b.item_id = :itemId " +
            "  AND i.owner_id = :ownerId " +
            "  AND b.start_time >= now() " +
            "  AND b.state = 'APPROVED' " +
            "ORDER BY b.start_time ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Booking getNextBooking(Long itemId, Long ownerId);

}
