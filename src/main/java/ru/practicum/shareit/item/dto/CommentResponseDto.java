package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private long id;
    private String text;
    private String authorName;
    private String created;

}
