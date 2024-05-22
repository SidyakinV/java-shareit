package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto dto
    ) {
        log.info("Получен запрос на бронирование от пользователя с id={}: {}", userId, dto);
        dto.setUserId(userId);
        Booking booking = bookingService.addBooking(dto);
        return BookingMapper.mapBookingToResponse(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info(
                "Получен запрос на подтверждение бронирования (bookingId={}) от пользователя userId={}, " +
                        "статус {}", bookingId, userId, approved);
        Booking booking = bookingService.approveBooking(userId, bookingId, approved);
        return BookingMapper.mapBookingToResponse(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingInfo(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info(
                "Получен запрос на получение информации о бронировании (bookingId={}) от пользователя userId={}",
                bookingId, userId);
        Booking booking = bookingService.getBookingInfo(userId, bookingId);
        return BookingMapper.mapBookingToResponse(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Pageable pageable;
        BookingState bookingState = BookingState.stringToBookingState(state);
        if (from != null && bookingState == BookingState.ALL) {
            log.info(
                    "Получен запрос на получение списка бронирования вещей пользователем с id={} " +
                            "и пагинацией (from={}, size={})", userId, from, size);
            pageable = PageRequest.of(from / size, size);
        } else {
            log.info(
                    "Получен запрос на получение списка бронирования вещей пользователем с id={} " +
                            "и статусом бронирования {}", userId, state);
            pageable = Pageable.unpaged();
        }
        List<Booking> list = bookingService.getUserBookings(userId, bookingState, pageable);
        return BookingMapper.convertBookingToResponseList(list);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Pageable pageable;
        BookingState bookingState = BookingState.stringToBookingState(state);
        if (from != null && bookingState == BookingState.ALL) {
            log.info(
                    "Получен запрос на получение списка бронирования вещей, принадлежащих владельцу с id={} " +
                            "и пагинацией (from={}, size={})", ownerId, from, size);
            pageable = PageRequest.of(from / size, size);
        } else {
            log.info(
                    "Получен запрос на получение списка бронирования вещей, принадлежащих владельцу с id={} " +
                            "и статусом бронирования {}", ownerId, state);
            pageable = Pageable.unpaged();
        }
        List<Booking> list = bookingService.getOwnerBookings(ownerId, bookingState, pageable);
        return BookingMapper.convertBookingToResponseList(list);
    }

}
