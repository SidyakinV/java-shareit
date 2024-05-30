package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingUser;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingJsonTests {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testJsonBookingDto() throws JsonProcessingException {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(1);

        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setUserId(2L);

        String expected = String.format(
                "{\"itemId\":%d,\"start\":\"%s\",\"end\":\"%s\",\"userId\":%d}",
                dto.getItemId(), dto.getStart(), dto.getEnd(), dto.getUserId());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test void testBookingResponseDto() throws JsonProcessingException {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setStatus(String.valueOf(BookingState.WAITING));

        BookingUser booker = new BookingUser();
        booker.setId(2L);
        dto.setBooker(booker);

        BookingItem item = new BookingItem();
        item.setId(3L);
        item.setName("Супер-вещь");
        dto.setItem(item);

        String expected = String.format(
                "{\"id\":%d,\"start\":\"%s\",\"end\":\"%s\",\"status\":\"%s\",\"booker\":" +
                "{\"id\":%d},\"item\":{\"id\":%d,\"name\":\"%s\"}}",
                dto.getId(), dto.getStart(), dto.getEnd(), dto.getStatus(),
                dto.getBooker().getId(), dto.getItem().getId(), dto.getItem().getName());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

}
