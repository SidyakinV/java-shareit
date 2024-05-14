package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final JpaBookingRepository bookingRepository;

    @Override
    public Booking addBooking(Booking booking) {
        checkUserExists(booking.getUserId());
        booking.setItem(getItemById(booking.getItemId()));
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException(
                    new Violation("Available", "Данная вещь недоступна для бронирования!"));
        }
        if (Objects.equals(booking.getItem().getOwnerId(), booking.getUserId())) {
            throw new NotFoundException(
                    new Violation("Owner", "Вещь бронируется владельцем!"));
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    new Violation("EndDate", "Некорректная дата завершения бронирования!"));
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException(
                    new Violation("StartDate", "Некорректная дата начала бронирования!"));
        }

        booking.setState(BookingState.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Добавлено бронирование: {}", savedBooking);
        return savedBooking;
    }

    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException(
                    new Violation("OwnerID", "Подтверждение бронирования выполняется владельцем вещи"));
        }
        if (booking.getState() != BookingState.WAITING) {
            throw new ValidationException(
                    new Violation("State", "Подтверждение бронирования возможно только для статуса WAITING"));
        }

        if (approved) {
            booking.setState(BookingState.APPROVED);
            // booking.getItem().setAvailable(false); // ?? не надо ??
        } else {
            booking.setState(BookingState.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Подтверждение бронирования: {}", savedBooking);
        return savedBooking;
    }

    @Override
    public Booking getBookingInfo(long userId, long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getUserId() != userId && booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException(
                    new Violation("info",
                            "Просмотр доступен владельцу вещи или пользователю, создавшего бронирование"));

        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(long userId, BookingState state) {
        checkUserExists(userId);
        switch (state) {
            case ALL:
                return bookingRepository.getUserBookings(userId, null);
            case WAITING:
            case REJECTED:
                return bookingRepository.getUserBookings(userId, state);
            default:
                return filterBookings(bookingRepository.getUserBookings(userId, BookingState.APPROVED), state);
        }
    }

    @Override
    public List<Booking> getOwnerBookings(long ownerId, BookingState state) {
        checkUserExists(ownerId);
        switch (state) {
            case ALL:
                return bookingRepository.getOwnerBookings(ownerId, null);
            case WAITING:
            case REJECTED:
                return bookingRepository.getOwnerBookings(ownerId, state);
            default:
                return filterBookings(bookingRepository.getOwnerBookings(ownerId, BookingState.APPROVED), state);
        }
    }

    @Override
    public List<Booking> getAllTest() {
        return bookingRepository.findAll();
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("user", String.format("Пользователь с id=%d не найден!", userId))));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("item", String.format("Вещь с id=%d не найдена!", itemId))));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("Booking", String.format("Не найдено бронирование с id=%d!", bookingId))));
    }

    private List<Booking> filterBookings(List<Booking> bookings, BookingState state) {
        switch (state) {
            case PAST:
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> !(LocalDateTime.now().isBefore(booking.getStart()) &&
                                LocalDateTime.now().isAfter(booking.getEnd())))
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

}
