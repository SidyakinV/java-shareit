package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking mapDtoToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse(dto.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        booking.setEnd(LocalDateTime.parse(dto.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return booking;
    }

    public static BookingResponseDto mapBookingToResponse(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setStatus(booking.getState().toString());

        BookingUser user = new BookingUser();
        user.setId(booking.getUser().getId());
        dto.setBooker(user);

        BookingItem item = new BookingItem();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        dto.setItem(item);

        return dto;
    }

    public static List<BookingResponseDto> convertBookingToResponseList(List<Booking> list) {
        return list.stream()
                .map(BookingMapper::mapBookingToResponse)
                .collect(Collectors.toList());
    }

}
