package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Data
public class RequestWithAnswerDto {
    private Long id;
    private String description;
    private String created;
    private List<ItemResponseDto> items;
}
