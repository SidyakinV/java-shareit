package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(ItemDto dto);

    Item updateItem(ItemDto dto);

    Item getItem(Long itemId, Long userId);

    List<Item> getOwnerItems(Long userId);

    List<Item> searchItems(String text);

    Comment addComment(CommentDto dto);

}
