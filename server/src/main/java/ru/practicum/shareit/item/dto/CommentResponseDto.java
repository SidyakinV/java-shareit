package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentResponseDto {

    private long id;
    private String text;
    private String authorName;
    private String created;

}
