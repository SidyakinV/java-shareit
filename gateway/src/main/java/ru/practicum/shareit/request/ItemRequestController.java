package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemRequestDto dto
    ) {
        log.info("POST add item request with userId {}, dto {}", userId, dto);
        return requestClient.addItemRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(
            @RequestHeader ("X-Sharer-User-Id") Long userId
    ) {
        log.info("GET list own item-requests with userId {}", userId);
        return requestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) @Valid @Min(0) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info(
                "GET list users item-requests with userId {}, from {}, size {}",
                userId, from, size);
        return requestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        log.info("GET view item-request with requestId {}, userId {}", requestId, userId);
        return requestClient.getItemRequest(userId, requestId);
    }

}
