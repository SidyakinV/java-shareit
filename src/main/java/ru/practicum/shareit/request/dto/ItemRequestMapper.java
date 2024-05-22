package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestResponseDto mapRequestToResponse(ItemRequest itemRequest) {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public static ItemRequest mapDtoToRequest(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        return itemRequest;
    }

    public static RequestWithAnswerDto mapRequestWithAnswerDto(ItemRequest itemRequest, List<ItemResponseDto> items) {
        RequestWithAnswerDto dto = new RequestWithAnswerDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(items);
        return dto;
    }

}
