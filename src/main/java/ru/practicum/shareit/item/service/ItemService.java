package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(ItemDto dto);

    Item updateItem(ItemDto dto);

    ItemResponseDto getItem(Long itemId, Long userId);

    List<ItemResponseDto> getOwnerItems(Long userId);

    List<ItemResponseDto> searchItems(String text);

    Comment addComment(CommentDto dto);

}
