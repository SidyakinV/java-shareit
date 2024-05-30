package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequestDto dto);

    List<RequestWithAnswerDto> getOwnItemRequests(Long userId);

    List<RequestWithAnswerDto> getAllItemRequests(Long userId, Pageable pageable);

    RequestWithAnswerDto getItemRequest(Long userId, Long requestId);

}
