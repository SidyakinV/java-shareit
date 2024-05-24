package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Booking addBooking(BookingDto dto) {
        Booking booking = BookingMapper.mapDtoToBooking(dto);
        booking.setUser(getUserById(dto.getUserId()));
        booking.setItem(getItemById(dto.getItemId()));

        if (!booking.getItem().getAvailable()) {
            throw new ValidationException(
                    new Violation("Available", "Данная вещь недоступна для бронирования!"));
        }
        if (Objects.equals(booking.getItem().getOwner().getId(), booking.getUser().getId())) {
            throw new NotFoundException(
                    new Violation("Owner", "Вещь бронируется владельцем!"));
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    new Violation("StartDate", "Некорректная дата начала бронирования!"));
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new ValidationException(
                    new Violation("EndDate", "Некорректная дата завершения бронирования!"));
        }

        booking.setState(BookingState.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Добавлено бронирование: {}", savedBooking);
        return savedBooking;
    }

    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(
                    new Violation("OwnerID", "Подтверждение бронирования выполняется владельцем вещи"));
        }
        if (booking.getState() != BookingState.WAITING) {
            throw new ValidationException(
                    new Violation("State", "Подтверждение бронирования возможно только для статуса WAITING"));
        }

        if (approved) {
            booking.setState(BookingState.APPROVED);
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
        if (booking.getUser().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(
                    new Violation("info",
                            "Просмотр доступен владельцу вещи или пользователю, создавшего бронирование"));

        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(long userId, BookingState state, Pageable pageable) {
        getUserById(userId);
        switch (state) {
            case ALL:
                return bookingRepository.getUserBookings(userId, null, pageable).getContent();
            case WAITING:
            case REJECTED:
                return bookingRepository.getUserBookings(userId, state, pageable).getContent();
            default:
                return filterBookings(bookingRepository.getUserBookings(userId, null, pageable).getContent(), state);
        }
    }

    @Override
    public List<Booking> getOwnerBookings(long ownerId, BookingState state, Pageable pageable) {
        getUserById(ownerId);
        switch (state) {
            case ALL:
                return bookingRepository.getOwnerBookings(ownerId, null, pageable).getContent();
            case WAITING:
            case REJECTED:
                return bookingRepository.getOwnerBookings(ownerId, state, pageable).getContent();
            default:
                return filterBookings(bookingRepository.getOwnerBookings(ownerId, null, pageable).getContent(), state);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
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

    private BookingState getBookingPeriodState(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            return BookingState.PAST;
        } else if (booking.getStart().isAfter(LocalDateTime.now())) {
            return BookingState.FUTURE;
        } else {
            return BookingState.CURRENT;
        }
    }

    private List<Booking> filterBookings(List<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> getBookingPeriodState(booking) == state)
                .collect(Collectors.toList());
    }

}
