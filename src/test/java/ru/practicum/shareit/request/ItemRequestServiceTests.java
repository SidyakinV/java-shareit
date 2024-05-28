package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTests {

    private final EntityManager em;
    private final ItemRequestService requestService;

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
    public void addItemRequest_success() {
        User user = newUser("newuser@mail.ru");
        em.persist(user);

        ItemRequestDto dto = newItemRequestDto(user.getId(), "Запрос вещи");

        requestService.addItemRequest(dto);

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT r FROM ItemRequest r WHERE r.user.id = :userId", ItemRequest.class)
                .setParameter("userId", user.getId());
        List<ItemRequest> list = query.getResultList();

        assertEquals(1, list.size());
        assertEquals(dto.getDescription(), list.get(0).getDescription());
    }

    @Test
    public void getOwnItemRequests_success() {
        List<RequestWithAnswerDto> list = requestService.getOwnItemRequests(testUser.getId());
        assertEquals(2, list.size());
    }

    @Test
    public void getAllItemRequests_success() {
        List<RequestWithAnswerDto> list = requestService.getAllItemRequests(testUser.getId(), Pageable.unpaged());
        assertEquals(3, list.size());
    }

    @Test
    public void getItemRequest_success() {
        ItemRequest request = newItemRequest(testUser, "Запрос");
        em.persist(request);

        RequestWithAnswerDto answer = requestService.getItemRequest(testUser.getId(), request.getId());

        assertNotNull(answer);
        assertEquals(request.getDescription(), answer.getDescription());
    }

    private ItemRequestDto newItemRequestDto(Long userId, String text) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setUserId(userId);
        dto.setDescription(text);
        return dto;
    }

    private ItemRequest newItemRequest(User user, String text) {
        ItemRequest request = new ItemRequest();
        request.setUser(user);
        request.setDescription(text);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь 1");
        user.setEmail(email);
        return user;
    }

}
