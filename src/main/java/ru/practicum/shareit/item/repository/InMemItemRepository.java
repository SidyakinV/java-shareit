package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemItemRepository implements ItemRepository {

    private final Map<Long, Item> itemsList = new HashMap<>();
    private long lastId = 0;

    @Override
    public Item addItem(Item item) {
        item.setId(++lastId);
        itemsList.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item savedItem = itemsList.get(item.getId());

        if (savedItem != null) {
            if (!savedItem.getOwnerId().equals(item.getOwnerId())) {
                throw new NotFoundException(new Violation("owner",
                        "Редактировать информацию о вещи может только ее владелец!"));
            }

            // Обновляются только заполненные поля
            if (item.getName() != null) {
                savedItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                savedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                savedItem.setAvailable(item.getAvailable());
            }
        }

        return savedItem;
    }

    @Override
    public Item getItem(long itemId) {
        return itemsList.get(itemId);
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        return itemsList.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String searchText = text.toLowerCase();
        return itemsList.values().stream()
                .filter(item ->
                        item.getAvailable() && (
                                item.getName().toLowerCase().contains(searchText) ||
                                item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }

}
