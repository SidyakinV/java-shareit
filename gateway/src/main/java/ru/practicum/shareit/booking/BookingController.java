package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	// POST /bookings - добавление бронирования
	@PostMapping
	public ResponseEntity<Object> addBooking(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto
	) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.addBooking(userId, requestDto);
	}

	// PATCH /bookings/{bookingId}?approved={approved} - подтверждение бронирования
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable Long bookingId,
			@RequestParam Boolean approved
	) {
		log.info(
				"Approving booking with bookingId {} from userId {}, approved {}",
				bookingId, userId, approved);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	// GET /bookings/{bookingId} - получение данных о бронировании
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId
	) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	// GET /bookings?state={state} - получение списка всех бронирований текущего пользователя
	@GetMapping
	public ResponseEntity<Object> getUserBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get user booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	// GET /bookings/owner?state={state} - получение списка бронирований для всех вещей владельца
	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get owner booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}

}


