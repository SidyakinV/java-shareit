package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item addItem(Item item);
    public Item updateItem(Item item);
    public Item getItem(Long itemId);
    public List<Item> getOwnerItems(Long userId);
    public List<Item> searchItems(String text);

}
