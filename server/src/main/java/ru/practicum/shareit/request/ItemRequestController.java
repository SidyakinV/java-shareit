package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utility.PageCalc;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponseDto addItemRequest(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto dto
    ) {
        log.info("Получен запрос от на запрос описания вещи от пользователя с id={}: {}", userId, dto);
        dto.setUserId(userId);
        ItemRequest itemRequest = requestService.addItemRequest(dto);
        return ItemRequestMapper.mapRequestToResponse(itemRequest);
    }

    @GetMapping
    public List<RequestWithAnswerDto> getOwnItemRequests(
            @RequestHeader ("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на получение списка своих запросов вещей: userId={}", userId);
        return requestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestWithAnswerDto> getAllItemRequests(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info(
                "Получен запрос на получение списка всех запросов вещей: userId={}, " +
                "параметры пагинации: from={}, size={}",
                userId, from, size);
        return requestService.getAllItemRequests(userId, PageCalc.getPageable(from, size));
    }

    @GetMapping("/{requestId}")
    public RequestWithAnswerDto getItemRequest(
            @RequestHeader ("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        log.info("Получен запрос на просмотр запроса вещи: requestId={}, userId={}", requestId, userId);
        return requestService.getItemRequest(userId, requestId);
    }

}
