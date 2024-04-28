package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto dto) {
        log.info("Получен POST-запрос от пользователя с id={} на добавление вещи: {}", userId, dto);
        Item item = ItemMapper.mapDtoToItem(dto);
        item.setOwnerId(userId);
        item = itemService.addItem(item);
        return ItemMapper.mapItemToDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto dto) {
        log.info("Получен PATCH-запрос от пользователя с userId={} на изменение вещи itemId={}: {}",
                userId, itemId, dto);
        Item item = ItemMapper.mapDtoToItem(dto);
        item.setId(itemId);
        item.setOwnerId(userId);
        item = itemService.updateItem(item);
        return ItemMapper.mapItemToDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр информации о вещи с id={}", userId, itemId);
        Item item = itemService.getItem(itemId);
        return ItemMapper.mapItemToDto(item);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос от пользователя с id={} на получение списка его вещей", userId);
        List<Item> items = itemService.getOwnerItems(userId);
        return items.stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("Получен GET-запрос от пользователя с id={} на поиск вещей по ключевому слову '{}'", userId, text);
        List<Item> items = itemService.searchItems(text);
        return items.stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

}
