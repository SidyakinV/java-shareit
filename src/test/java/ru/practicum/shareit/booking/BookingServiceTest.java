package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    Map<Long, User> usersList = new HashMap<>();
    Map<Long, Item> itemsList = new HashMap<>();

    Map<Long, Booking> bookingsList = new HashMap<>();

    public BookingServiceTest() {
        usersList.put(1L, newUser(1L));
        usersList.put(2L, newUser(2L));
        usersList.put(3L, newUser(3L));

        itemsList.put(1L, newItem(1L, usersList.get(1L)));
        itemsList.put(2L, newItem(2L, usersList.get(2L)));
        itemsList.put(3L, newItem(3L, usersList.get(3L)));

        itemsList.get(2L).setAvailable(false);

        bookingsList.put(1L, newBooking(1L, usersList.get(1L), itemsList.get(2L)));
        bookingsList.put(2L, newBooking(2L, usersList.get(2L), itemsList.get(3L)));
        bookingsList.put(3L, newBooking(3L, usersList.get(3L), itemsList.get(1L)));

        bookingsList.get(2L).setState(BookingState.APPROVED);
        bookingsList.get(3L).setState(BookingState.REJECTED);
    }

    @BeforeEach
    public void init() {
        Mockito
                .lenient()
                .when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    User user = usersList.get(userId);
                    return user != null ? Optional.of(user) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long itemId = invocationOnMock.getArgument(0, Long.class);
                    Item item = itemsList.get(itemId);
                    return item != null ? Optional.of(item) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    Booking booking = bookingsList.get(bookingId);
                    return booking != null ? Optional.of(booking) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

    @Test
    public void addBooking_success() {
        BookingDto dto = newBookingDto();
        dto.setItemId(2L);
        Booking booking = bookingService.addBooking(dto);

        assertEquals(dto.getUserId(), booking.getUser().getId());
        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(BookingState.WAITING, booking.getState());
        assertEquals(dto.getStart(), booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertEquals(dto.getEnd(), booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    public void addBooking_fail_userNotFound() {
        BookingDto dto = newBookingDto();
        dto.setUserId(999L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_itemNotFound() {
        BookingDto dto = newBookingDto();
        dto.setItemId(999L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_itemNotAvailable() {
        BookingDto dto = newBookingDto();
        dto.setItemId(2L);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_bookingByOwner() {
        BookingDto dto = newBookingDto();

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_startBeforeNow() {
        BookingDto dto = newBookingDto();
        dto.setUserId(2L);
        dto.setStart(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_startEqualEnd() {
        BookingDto dto = newBookingDto();
        dto.setUserId(2L);
        dto.setStart(dto.getEnd());

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addBooking_fail_startAfterEnd() {
        BookingDto dto = newBookingDto();
        dto.setUserId(2L);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        LocalDateTime start = end.plusSeconds(1);
        dto.setStart(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void approve_fail_bookingNotFound() {
        final Exception exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(1L, 999L, true)
        );
        assertNotNull(exception);
    }

    @Test
    public void approve_fail_userNotOwner() {
        final Exception exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(1L, 2L, true)
        );
        assertNotNull(exception);
    }

    @Test
    public void approve_fail_statusNotWaiting() {
        final Exception exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approveBooking(3L, 2L, true)
        );
        assertNotNull(exception);
    }

    @Test
    public void approve_success_setStatusApproved() {
        Booking booking = bookingService.approveBooking(2L, 1L, true);
        assertEquals(BookingState.APPROVED, booking.getState());
    }

    @Test
    public void approve_success_setStatusRejected() {
        Booking booking = bookingService.approveBooking(2L, 1L, false);
        assertEquals(BookingState.REJECTED, booking.getState());
    }

    private BookingDto newBookingDto() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(1);

        BookingDto dto = new BookingDto();
        dto.setUserId(1L);
        dto.setItemId(1L);
        dto.setStart(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    private User newUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("User" + userId);
        return user;
    }

    private Item newItem(Long itemId, User owner) {
        Item item = new Item();
        item.setId(itemId);
        item.setName("Item" + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Booking newBooking(Long bookingId, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusHours(bookingId));
        booking.setEnd(booking.getStart().plusHours(2));
        booking.setState(BookingState.WAITING);
        booking.setUser(user);
        booking.setItem(item);
        return booking;
    }

}
