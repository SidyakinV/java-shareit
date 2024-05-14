package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long itemId, Long userId);

    List<Item> getOwnerItems(Long userId);

    List<Item> searchItems(String text);

    Comment addComment(Comment comment);

}
