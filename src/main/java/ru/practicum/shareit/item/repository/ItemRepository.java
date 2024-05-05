package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(long itemId);

    List<Item> getOwnerItems(long userId);

    List<Item> searchItems(String text);

}
