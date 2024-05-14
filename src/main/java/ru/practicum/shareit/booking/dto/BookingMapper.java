package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking mapDtoToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setItemId(dto.getItemId());
        booking.setStart(LocalDateTime.parse(dto.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        booking.setEnd(LocalDateTime.parse(dto.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return booking;
    }

    public static ResponseBookingDto mapBookingToResponse(Booking booking) {
        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getState().toString());

        BookingUser user = new BookingUser();
        user.setId(booking.getUserId());
        dto.setBooker(user);

        BookingItem item = new BookingItem();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        dto.setItem(item);

        return dto;
    }

    public static List<ResponseBookingDto> convertBookingToResponseList(List<Booking> list) {
        return list.stream()
                .map(BookingMapper::mapBookingToResponse)
                .collect(Collectors.toList());
    }

}