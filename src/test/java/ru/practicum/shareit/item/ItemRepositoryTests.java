package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner1;
    private User owner2;
    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    public void init() {
        owner1 = newUser("owner1@mail.ru");
        em.persist(owner1);

        owner2 = newUser("owner2@mail.ru");
        em.persist(owner2);

        user1 = newUser("user1@email.com");
        em.persist(user1);

        user2 = newUser("user2@email.com");
        em.persist(user2);

        Item item1 = newItem(owner1, "Сапоги-скороходы", "Очень быстрые САПОГИ (скороходы)");
        Item item2 = newItem(owner1, "Шапка-невидимка", "До сих пор никто найти не может");
        Item item3 = newItem(owner1, "Крестовая отвертка", "Отвертка и отвертка");
        Item item4 = newItem(owner2, "Дрель ударная", "Скоростная дрель");
        Item item5 = newItem(owner2, "Анемометр", "Измерение скорости ветра");
        Item item6 = newItem(owner2, "Дрель 'Малютка'", "Миниатюрная дрель");

        item5.setAvailable(false);

        request1 = newItemRequest(user1, "Хочу необычную вещь");
        em.persist(request1);

        request2 = newItemRequest(user2, "Дайте дрель!");
        em.persist(request2);

        item2.setRequestId(request1.getId());
        item4.setRequestId(request2.getId());
        item6.setRequestId(request2.getId());

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.persist(item5);
        em.persist(item6);
    }

    @Test
    public void searchItems_success() {
        List<Item> items = itemRepository.searchItems("СкОрО", Pageable.unpaged()).getContent();
        assertEquals(2, items.size());
    }

    @Test
    public void findByOwnerId_success() {
        List<Item> items = itemRepository.findByOwnerId(owner1.getId(), Pageable.unpaged()).getContent();
        assertEquals(3, items.size());
    }

    @Test
    public void findByRequestId_success() {
        List<Item> items = itemRepository.findByRequestId(request2.getId());
        assertEquals(2, items.size());
    }

    private Item newItem(User owner, String name, String description) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setRequestId(null);
        return item;
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Юзер");
        user.setEmail(email);
        return user;
    }

    private ItemRequest newItemRequest(User user, String description) {
        ItemRequest request = new ItemRequest();
        request.setUser(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription(description);
        return request;
    }

}
