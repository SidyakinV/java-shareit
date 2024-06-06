package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentDto {

    private String text;
    private Long itemId;
    private Long userId;

}
