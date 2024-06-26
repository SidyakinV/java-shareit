package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {

    @NotBlank
    private String text;

    private Long itemId;
    private Long userId;

}
