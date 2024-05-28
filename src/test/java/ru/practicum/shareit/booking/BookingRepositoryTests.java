package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void getUserBookings_allState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getUserBookings(user.getId(), null, Pageable.unpaged()).getContent();

        assertEquals(3, list.size());
    }

    @Test
    public void getUserBookings_waitingState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getUserBookings(user.getId(), BookingState.WAITING, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getUserBookings_approvedState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getUserBookings(user.getId(), BookingState.APPROVED, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getUserBookings_rejectedState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getUserBookings(user.getId(), BookingState.REJECTED, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getOwnerBookings_allState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getOwnerBookings(owner.getId(), null, Pageable.unpaged()).getContent();

        assertEquals(3, list.size());
    }

    @Test
    public void getOwnerBookings_waitingState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getOwnerBookings(owner.getId(), BookingState.WAITING, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getOwnerBookings_approvedState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getOwnerBookings(owner.getId(), BookingState.APPROVED, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getOwnerBookings_rejectedState_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        bookingRepository.save(newBooking(user, item, BookingState.WAITING));
        bookingRepository.save(newBooking(user, item, BookingState.APPROVED));
        bookingRepository.save(newBooking(user, item, BookingState.REJECTED));

        List<Booking> list = bookingRepository.getOwnerBookings(owner.getId(), BookingState.REJECTED, Pageable.unpaged()).getContent();

        assertEquals(1, list.size());
    }

    @Test
    public void getLastFinishedUserBooking_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        LocalDate currentDate = LocalDate.now();
        Booking booking1 = newBooking(user, item, BookingState.APPROVED);
        Booking booking2 = newBooking(user, item, BookingState.APPROVED);
        Booking booking3 = newBooking(user, item, BookingState.APPROVED);

        booking1.setEnd(currentDate.minusDays(3).atStartOfDay());
        em.persist(booking1);

        booking2.setEnd(currentDate.minusDays(2).atStartOfDay());
        em.persist(booking2);

        booking3.setEnd(currentDate.minusDays(4).atStartOfDay());
        em.persist(booking3);

        Booking lastBooking = bookingRepository.getLastFinishedUserBooking(user.getId(), item.getId());
        assertEquals(currentDate.minusDays(2).atStartOfDay(), lastBooking.getEnd());
    }

    @Test
    public void getLastBooking_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        LocalDate currentDate = LocalDate.now();
        Booking booking1 = newBooking(user, item, BookingState.APPROVED);
        Booking booking2 = newBooking(user, item, BookingState.APPROVED);
        Booking booking3 = newBooking(user, item, BookingState.APPROVED);

        booking1.setStart(currentDate.minusDays(3).atStartOfDay());
        em.persist(booking1);

        booking2.setStart(currentDate.minusDays(2).atStartOfDay());
        em.persist(booking2);

        booking3.setStart(currentDate.minusDays(4).atStartOfDay());
        em.persist(booking3);

        Booking lastBooking = bookingRepository.getLastBooking(item.getId(), owner.getId());
        assertEquals(currentDate.minusDays(2).atStartOfDay(), lastBooking.getStart());
    }

    @Test
    public void getNextBooking_success() {
        User user = userRepository.save(newUser("booking_repo_1@mail.ru"));
        User owner = userRepository.save(newUser("booking_repo_2@mail.ru"));
        Item item = itemRepository.save(newItem(owner));

        LocalDate currentDate = LocalDate.now();
        Booking booking1 = newBooking(user, item, BookingState.APPROVED);
        Booking booking2 = newBooking(user, item, BookingState.APPROVED);
        Booking booking3 = newBooking(user, item, BookingState.APPROVED);

        booking1.setStart(currentDate.plusDays(5).atStartOfDay());
        em.persist(booking1);

        booking2.setStart(currentDate.plusDays(3).atStartOfDay());
        em.persist(booking2);

        booking3.setStart(currentDate.plusDays(7).atStartOfDay());
        em.persist(booking3);

        Booking lastBooking = bookingRepository.getNextBooking(item.getId(), owner.getId());
        assertEquals(currentDate.plusDays(3).atStartOfDay(), lastBooking.getStart());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail(email);
        return user;
    }

    private Item newItem(User owner) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName("Какая-то вещь");
        item.setDescription("Описание к вещи");
        item.setAvailable(true);
        return item;
    }

    private Booking newBooking(User user, Item item, BookingState state) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setUser(user);
        booking.setState(state);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd((LocalDateTime.now().plusDays(1)));
        return booking;
    }

}
