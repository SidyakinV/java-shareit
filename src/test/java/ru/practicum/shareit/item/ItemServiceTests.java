package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;

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

    @Test
    public void getOwnerItems_success() {
        Long userId = init();
        List<ItemResponseDto> items = itemService.getOwnerItems(userId, Pageable.unpaged());

        assertEquals(2, items.size());
    }

    @Test
    public void searchItems_success() {
        init();
        List<ItemResponseDto> items = itemService.searchItems("СкОрО", Pageable.unpaged());

        assertEquals(2, items.size());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Пользователь 1");
        user.setEmail(email);
        return user;
    }

    private Item newItem(User owner, String name, String descrition) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(descrition);
        item.setAvailable(true);
        return item;
    }

    private Long init() {
        User owner1 = userService.addUser(newUser("itemservice1@mail.ru"));
        User owner2 = userService.addUser(newUser("itemservice2@mail.ru"));

        em.persist(newItem(owner1, "Отвертка крестовая", "Просто отвертка"));
        em.persist(newItem(owner1, "ДРЕЛЬ ударная", "Супер СКОРОСТНАЯ дрель"));
        em.persist(newItem(owner2, "Шуруповерт", "может использоваться как дрель"));
        em.persist(newItem(owner2, "Сапоги-скороходы", "Очень быстрые сапоги"));
        em.persist(newItem(owner2, "Шапка-невидимка", "До сих пор найти не можем"));

        return owner1.getId();
    }

}
