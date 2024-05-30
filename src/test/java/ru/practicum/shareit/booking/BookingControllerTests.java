package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
    }

    @Test
    public void addBooking() throws Exception {
        BookingDto dto = newBookingDto();
        Booking booking = newBooking(dto, 1L);

        Mockito
                .when(bookingService.addBooking(Mockito.any(BookingDto.class)))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void approveBooking() throws Exception {
        BookingDto dto = newBookingDto();
        Booking booking = newBooking(dto, 1L);

        Mockito
                .when(bookingService.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void getBookingInfo() throws Exception {
        BookingDto dto = newBookingDto();
        Booking booking = newBooking(dto, 1L);

        Mockito
                .when(bookingService.getBookingInfo(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

    }

    @Test
    public void getUserBookings() throws Exception {
        List<Booking> bookingList = getBookingList();
        List<BookingResponseDto> bookingResonseList = BookingMapper.convertBookingToResponseList(bookingList);

        Mockito
                .when(bookingService.getUserBookings(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResonseList)));
    }

    @Test
    public void getOwnerBookings() throws Exception {
        List<Booking> bookingList = getBookingList();
        List<BookingResponseDto> bookingResonseList = BookingMapper.convertBookingToResponseList(bookingList);

        Mockito
                .when(bookingService.getOwnerBookings(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResonseList)));
    }

    private BookingDto newBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setUserId(1L);
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    private Booking newBooking(BookingDto dto, Long id) {
        User user = new User();
        user.setId(100L + id);

        Item item = new Item();
        item.setId(200L + id);

        Booking booking = BookingMapper.mapDtoToBooking(dto);
        booking.setId(id);
        booking.setState(BookingState.WAITING);
        booking.setUser(user);
        booking.setItem(item);

        return booking;
    }

    private BookingResponseDto newBookingResponseDto(Long id) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(id);
        dto.setStatus(String.valueOf(BookingState.WAITING));
        dto.setStart(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    private List<Booking> getBookingList() {
        BookingDto dto = newBookingDto();
        Booking booking = newBooking(dto, 1L);

        List<Booking> list = new ArrayList<>();
        list.add(booking);
        return list;
    }

}
