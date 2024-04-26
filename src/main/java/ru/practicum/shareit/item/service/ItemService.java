package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);
    Item updateItem(Item item);
    Item getItem(Long itemId);
    List<Item> getOwnerItems(Long userId);
    List<Item> searchItems(String text);

}
