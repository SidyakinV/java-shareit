package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto dto
    ) {
        log.info("POST add item with userId {}, item {}", userId, dto);
        return itemClient.addItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto dto
    ) {
        log.info("PATCH update item with userId {}, itemId {}, item {}",
                userId, itemId, dto);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.info("GET view item info with userId {}, itemId {}", userId, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info(
                "GET view owner items with userId {}, from={}, size={}",
                userId, from, size);
        return itemClient.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info(
                "Search items with userId {}, text '{}', from {}, size {}",
                userId, text, from, size);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto dto) {
        log.info("Creating comment {} for itemId {} from userId {}", dto, userId, itemId);
        return itemClient.addComment(userId, itemId, dto);
    }

}
