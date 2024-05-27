package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTests {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void addBooking_success() {
        User user = userService.addUser(newUser("booking_user_1@email.com"));
        User owner = userService.addUser(newUser("booking_user_2@email.com"));
        Item item = itemService.addItem(newItemDto(owner.getId()));

        BookingDto dto = newBookingDto(user, item, LocalDateTime.now().plusMinutes(5));
        bookingService.addBooking(dto);

        TypedQuery<Booking> query = em
                .createQuery(
                        "SELECT b FROM Booking b " +
                                " JOIN FETCH b.user u " +
                                "WHERE u.id = :userId", Booking.class)
                .setParameter("userId", user.getId());
        Booking booking = query.getSingleResult();

        assertEquals(booking.getUser().getId(), user.getId());
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getState(), BookingState.WAITING);
        assertEquals(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), dto.getStart());
        assertEquals(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), dto.getEnd());
    }

    @Test
    public void getUserBooking_byAllState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.ALL, Pageable.unpaged());

        assertEquals(5, booking.size());
    }

    @Test
    public void getUserBooking_byCurrentState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.CURRENT, Pageable.unpaged());

        assertEquals(1, booking.size());
    }

    @Test
    public void getUserBooking_byPastState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.PAST, Pageable.unpaged());

        assertEquals(1, booking.size());
    }

    @Test
    public void getUserBooking_byFutureState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.FUTURE, Pageable.unpaged());

        assertEquals(3, booking.size());
    }

    @Test
    public void getUserBooking_byWaitingState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.WAITING, Pageable.unpaged());

        assertEquals(1, booking.size());
    }

    @Test
    public void getUserBooking_byRejectedState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.REJECTED, Pageable.unpaged());

        assertEquals(1, booking.size());
    }

    @Test
    public void getUserBooking_byApprovedState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getUserBookings(userId, BookingState.APPROVED, Pageable.unpaged());

        assertEquals(3, booking.size());
    }

    @Test
    public void getOwnerBooking_byAllState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.ALL, Pageable.unpaged());

        assertEquals(6, booking.size());
    }

    @Test
    public void getOwnerBooking_byCurrentState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.CURRENT, Pageable.unpaged());

        assertEquals(2, booking.size());
    }

    @Test
    public void getOwnerBooking_byPastState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.PAST, Pageable.unpaged());

        assertEquals(2, booking.size());
    }

    @Test
    public void getOwnerBooking_byFutureState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.FUTURE, Pageable.unpaged());

        assertEquals(2, booking.size());
    }

    @Test
    public void getOwnerBooking_byWaitingState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.WAITING, Pageable.unpaged());

        assertEquals(1, booking.size());
    }

    @Test
    public void getOwnerBooking_byRejectedState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.REJECTED, Pageable.unpaged());

        assertEquals(2, booking.size());
    }

    @Test
    public void getOwnerBooking_byApprovedState_success() {
        Long userId = initBookingList();
        List<Booking> booking = bookingService.getOwnerBookings(userId, BookingState.APPROVED, Pageable.unpaged());

        assertEquals(3, booking.size());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь 1");
        user.setEmail(email);
        return user;
    }

    private ItemDto newItemDto(Long ownerId) {
        ItemDto dto = new ItemDto();
        dto.setName("Название вещи");
        dto.setDescription("Описание вещи");
        dto.setAvailable(true);
        dto.setOwnerId(ownerId);
        return dto;
    }

    private BookingDto newBookingDto(User user, Item item, LocalDateTime start) {
        BookingDto dto = new BookingDto();
        dto.setUserId(user.getId());
        dto.setItemId(item.getId());
        dto.setStart(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setEnd(start.plusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    private Booking newBooking(User user, Item item, LocalDateTime start, BookingState state) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(start.plusHours(2));
        booking.setState(state);
        booking.setUser(user);
        booking.setItem(item);
        return booking;
    }

    private Long initBookingList() {
        User user1 = userService.addUser(newUser("test_booking1@mail.ru"));
        User user2 = userService.addUser(newUser("test_booking2@mail.ru"));
        User user3 = userService.addUser(newUser("test_booking3@mail.ru"));

        Item item1 = itemService.addItem(newItemDto(user1.getId()));
        Item item2 = itemService.addItem(newItemDto(user2.getId()));
        Item item3 = itemService.addItem(newItemDto(user3.getId()));

        em.persist(newBooking(user1, item2, LocalDateTime.now().minusDays(5), BookingState.APPROVED));
        em.persist(newBooking(user1, item2, LocalDateTime.now().minusHours(1), BookingState.APPROVED));
        em.persist(newBooking(user1, item2, LocalDateTime.now().plusDays(3), BookingState.WAITING));
        em.persist(newBooking(user1, item3, LocalDateTime.now().plusHours(1), BookingState.APPROVED));
        em.persist(newBooking(user1, item3, LocalDateTime.now().plusDays(10), BookingState.REJECTED));

        em.persist(newBooking(user2, item1, LocalDateTime.now().minusDays(5), BookingState.APPROVED));
        em.persist(newBooking(user2, item1, LocalDateTime.now().minusHours(1), BookingState.APPROVED));
        em.persist(newBooking(user2, item1, LocalDateTime.now().plusDays(3), BookingState.WAITING));
        em.persist(newBooking(user3, item1, LocalDateTime.now().minusDays(1), BookingState.APPROVED));
        em.persist(newBooking(user3, item1, LocalDateTime.now().minusHours(1), BookingState.REJECTED));
        em.persist(newBooking(user3, item1, LocalDateTime.now().plusDays(10), BookingState.REJECTED));

        return user1.getId();
    }

}
