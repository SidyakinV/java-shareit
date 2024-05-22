package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestWithAnswerDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}