package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User testUser;

    @BeforeEach
    public void init() {
        User user1 = newUser("user1@mail.ru");
        em.persist(user1);

        User user2 = newUser("user2@mail.ru");
        em.persist(user2);

        em.persist(newItemRequest(user1, "Первый"));
        em.persist(newItemRequest(user2, "Второй"));

        testUser = newUser("testuser@mail.ru");
        em.persist(testUser);

        em.persist(newItemRequest(testUser, "Третий"));
        em.persist(newItemRequest(testUser, "Четвертый"));
    }

    @Test
    public void findByUser_success() {
        List<ItemRequest> list = requestRepository.findByUserOrderByCreatedDesc(testUser);
        assertEquals(2, list.size());
    }

    @Test
    public void findWithoutUser_success() {
        List<ItemRequest> list =
                requestRepository.findByUserNotOrderByCreatedDesc(testUser, Pageable.unpaged()).getContent();
        assertEquals(2, list.size());
    }

    @Test
    public void findItemRequestById_success() {
        ItemRequest request = newItemRequest(testUser, "Запрос");
        em.persist(request);

        Optional<ItemRequest> savedRequest = requestRepository.findItemRequestById(request.getId());
        assertTrue(savedRequest.isPresent());
    }

    @Test
    public void findItemRequestById_fail_wrongId() {
        ItemRequest request = newItemRequest(testUser, "Запрос");
        em.persist(request);

        Optional<ItemRequest> savedRequest = requestRepository.findItemRequestById(request.getId() + 1000L);
        assertTrue(savedRequest.isEmpty());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь 1");
        user.setEmail(email);
        return user;
    }

    private ItemRequest newItemRequest(User user, String text) {
        ItemRequest request = new ItemRequest();
        request.setUser(user);
        request.setDescription(text);
        request.setCreated(LocalDateTime.now());
        return request;
    }

}
