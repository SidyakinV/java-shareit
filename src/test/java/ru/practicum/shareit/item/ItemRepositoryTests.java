package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void searchItems_success() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("user@mail.ru");
        em.persist(owner);

        em.persist(newItem(owner, "Сапоги-скороходы", "Очень быстрые САПОГИ (скороходы)", true));
        em.persist(newItem(owner, "Шапка-невидимка", "До сих пор никто найти не может", true));
        em.persist(newItem(owner, "Крестовая отвертка", "Отвертка и отвертка", true));
        em.persist(newItem(owner, "Дрель ударная", "Скоростная дрель", true));
        em.persist(newItem(owner, "Анемометр", "Измерение скорости ветра", false));

        List<Item> items = itemRepository.searchItems("СкОрО", Pageable.unpaged()).getContent();
        assertEquals(2, items.size());
    }

    private Item newItem(User owner, String name, String description, boolean available) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        return item;
    }

}
