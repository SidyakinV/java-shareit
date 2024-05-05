package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым!")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым!")
    private String description;

    @NotNull(message = "Не задана доступность вещи для аренды!")
    private Boolean available;

}
