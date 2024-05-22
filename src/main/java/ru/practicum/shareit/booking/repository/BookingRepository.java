package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "  JOIN b.item " +
            "  JOIN b.user " +
            "WHERE b.user.id = :userId " +
            "  AND (:state is null OR b.state = :state) " +
            "ORDER BY b.start DESC")
    Slice<Booking> getUserBookings(long userId, BookingState state, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "  JOIN b.item AS i " +
            "  JOIN b.user AS u " +
            "WHERE i.owner.id = :ownerId " +
            "  AND (:state is null OR b.state = :state) " +
            "ORDER BY b.start DESC")
    Slice<Booking> getOwnerBookings(long ownerId, BookingState state, Pageable pageable);

    @Query(value =
            "SELECT b.* " +
                    "FROM bookings AS b " +
                    "WHERE b.item_id = :itemId " +
                    "  AND b.user_id = :userId " +
                    "  AND b.end_time < now() " +
                    "  AND b.state = 'APPROVED' " +
                    "ORDER BY b.id DESC " +
                    "LIMIT 1",
            nativeQuery = true)
    Booking getFinishedUserBooking(Long userId, Long itemId);

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
