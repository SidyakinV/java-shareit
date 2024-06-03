package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTests {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    private User owner1;
    private User user1;
    private Item testItem;


    @BeforeEach
    public void init() {
        owner1 = userService.addUser(newUser("owner1@mail.ru"));
        User owner2 = userService.addUser(newUser("owner2@mail.ru"));

        user1 = userService.addUser(newUser("user1@mail.ru"));
        User user2 = userService.addUser(newUser("user2@mail.ru"));

        em.persist(newItem(owner1, "Отвертка крестовая", "Просто отвертка"));
        em.persist(newItem(owner1, "ДРЕЛЬ ударная", "Супер СКОРОСТНАЯ дрель"));
        em.persist(newItem(owner2, "Шуруповерт", "может использоваться как дрель"));
        em.persist(newItem(owner2, "Сапоги-скороходы", "Очень быстрые сапоги"));
        em.persist(newItem(owner2, "Шапка-невидимка", "До сих пор найти не можем"));

        testItem = newItem(owner2, "Тестовая вещь", "Вещь для тестов");
        em.persist(testItem);

        em.persist(newComment(user1, testItem));
        em.persist(newComment(user2, testItem));
    }

    @Test
    public void addItem_success() {
        ItemDto dto = newItemDto(owner1.getId());
        Item item = itemService.addItem(dto);

        Item savedItem = getSavedItem(item.getId());

        assertEquals(dto.getName(), savedItem.getName());
        assertEquals(dto.getDescription(), savedItem.getDescription());
        assertEquals(dto.getAvailable(), savedItem.getAvailable());
        assertEquals(dto.getOwnerId(), savedItem.getOwner().getId());
    }

    @Test
    public void updateItem_onlyName_success() {
        ItemDto dto = new ItemDto();
        dto.setId(testItem.getId());
        dto.setOwnerId(testItem.getOwner().getId());
        dto.setName("Новое имя");

        String oldDescription = testItem.getDescription();

        itemService.updateItem(dto);
        Item savedItem = getSavedItem(dto.getId());

        assertEquals(dto.getName(), savedItem.getName());
        assertEquals(oldDescription, savedItem.getDescription());
    }

    @Test
    public void getItem_forUser_success() {
        ItemResponseDto dto = itemService.getItem(testItem.getId(), user1.getId());

        assertEquals(testItem.getName(), dto.getName());
        assertEquals(testItem.getDescription(), dto.getDescription());
        assertEquals(testItem.getAvailable(), dto.getAvailable());
        assertEquals(2, dto.getComments().size());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    public void getOwnerItems_success() {
        List<ItemResponseDto> items = itemService.getOwnerItems(owner1.getId(), Pageable.unpaged());
        assertEquals(2, items.size());
    }

    @Test
    public void searchItems_success() {
        List<ItemResponseDto> items = itemService.searchItems("СкОрО", Pageable.unpaged());
        assertEquals(2, items.size());
    }

    @Test
    public void addComment_success() {
        User user = newUser("user3@mail.ru");
        em.persist(user);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setState(BookingState.APPROVED);
        booking.setItem(testItem);
        booking.setUser(user);
        em.persist(booking);

        CommentDto dto = new CommentDto();
        dto.setText("Новый комментарий");
        dto.setItemId(testItem.getId());
        dto.setUserId(user.getId());

        itemService.addComment(dto);

        TypedQuery<Comment> query = em
                .createQuery("SELECT c FROM Comment c WHERE c.author.id = :userId", Comment.class)
                .setParameter("userId", user.getId());
        List<Comment> comments = query.getResultList();

        assertEquals(1, comments.size());
        assertEquals(dto.getText(), comments.get(0).getText());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь 1");
        user.setEmail(email);
        return user;
    }

    private Item newItem(User owner, String name, String description) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return item;
    }

    private ItemDto newItemDto(Long ownerId) {
        ItemDto dto = new ItemDto();
        dto.setName("Вещь");
        dto.setDescription("Описание");
        dto.setAvailable(true);
        dto.setOwnerId(ownerId);
        return dto;
    }

    private Comment newComment(User author, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText("Комментарий");
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    private Item getSavedItem(Long id) {
        TypedQuery<Item> query = em
                .createQuery("SELECT i FROM Item i WHERE i.id = :itemId", Item.class)
                .setParameter("itemId", id);
        return query.getSingleResult();
    }

}
