package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {

    @NotNull
    private String description;

    private Long userId;

}
