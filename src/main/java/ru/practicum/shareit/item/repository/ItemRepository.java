package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    public Item addItem(Item item);
    public Item updateItem(Item item);
    public Item getItem(long itemId);
    public List<Item> getOwnerItems(long userId);
    public List<Item> searchItems(String text);

}
