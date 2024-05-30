package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTests {

    private final EntityManager em;
    private final UserService userService;

    @Test
    public void addUser_success() {
        User user = newUser("mail@email.com");

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", user.getEmail());

        List<User> users = query.getResultList();
        assertTrue(users.size() < 1);

        userService.addUser(user);

        users = query.getResultList();
        assertEquals(1, users.size());

        User savedUser = users.get(0);
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void addUser_fail_duplicateEmail() {
        userService.addUser(newUser("user1@mail.ru"));

        final Exception exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.addUser(newUser("user1@mail.ru"))
        );
        assertNotNull(exception);
    }

    @Test
    public void updateUser_success() {
        User oldUser = userService.addUser(newUser("user1@mail.ru"));

        User user = new User();
        user.setId(oldUser.getId());
        user.setName("Пользователь");
        user.setEmail("new@email.com");

        userService.updateUser(user);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User savedUser = query.setParameter("id", user.getId()).getSingleResult();

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void updateUser_fail_blankName() {
        User user = newUser("user1@mail.ru");
        em.persist(user);

        user.setName(" ");

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user));
        assertNotNull(exception);
    }

    @Test
    public void updateUser_fail_blankEmail() {
        User user = newUser("user1@mail.ru");
        em.persist(user);

        user.setEmail(" ");

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user));
        assertNotNull(exception);
    }

    @Test
    public void updateUser_fail_badEmail() {
        User user = newUser("user1@mail.ru");
        em.persist(user);

        user.setEmail("vasya.pupkin");

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user));
        assertNotNull(exception);
    }

    @Test
    public void getUser_success() {
        User user = newUser("user1@mail.ru");
        em.persist(user);

        User savedUser = userService.getUser(user.getId());

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void deleteUser_success() {
        User user = newUser("user1@mail.ru");
        em.persist(user);

        userService.deleteUser(user.getId());

        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", user.getId());
        List<User> users = query.getResultList();

        assertEquals(0, users.size());
    }

    @Test
    public void getAllUser_success() {
        int count = userService.getAllUsers().size();

        userService.addUser(newUser("user1@mail.ru"));
        userService.addUser(newUser("user2@mail.ru"));
        userService.addUser(newUser("user3@mail.ru"));

        List<User> users = userService.getAllUsers();
        assertEquals(count + 3, users.size());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Имя пользователя");
        user.setEmail(email);
        return user;
    }

}
