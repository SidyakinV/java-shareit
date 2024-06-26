package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.PageCalc;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody ItemDto dto) {
        log.info("Получен POST-запрос от пользователя с id={} на добавление вещи: {}", userId, dto);
        dto.setOwnerId(userId);
        Item item = itemService.addItem(dto);
        return ItemMapper.mapItemToDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody ItemDto dto) {
        log.info("Получен PATCH-запрос от пользователя с userId={} на изменение вещи itemId={}: {}",
                userId, itemId, dto);
        dto.setId(itemId);
        dto.setOwnerId(userId);
        Item item = itemService.updateItem(dto);
        return ItemMapper.mapItemToDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр информации о вещи с id={}", userId, itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getOwnerItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info(
                "Получен GET-запрос от пользователя с id={} на получение списка его вещей, " +
                "параметры пагинации (from={}, size={})",
                userId, from, size);
        return itemService.getOwnerItems(userId, PageCalc.getPageable(from, size));
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info("Получен GET-запрос от пользователя с id={} на поиск вещей по ключевому слову '{}', " +
                "параметры пагинации: from={}, size={}", userId, text, from, size);
        return itemService.searchItems(text, PageCalc.getPageable(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto dto) {
        log.info(
                "Получен POST-запрос на добавление комментария пользователем с userId={} к вещи с itemId={}: {}",
                userId, itemId, dto);
        dto.setItemId(itemId);
        dto.setUserId(userId);
        Comment comment = itemService.addComment(dto);
        return CommentMapper.mapCommentToResponseDto(comment);
    }

}
