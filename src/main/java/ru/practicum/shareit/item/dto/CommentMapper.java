package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static Comment mapDtoToComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }

    public static CommentResponseDto mapCommentToResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getAuthor().getName());
        return dto;
    }

    public static List<CommentResponseDto> mapCommentsToListDto(List<Comment> comments) {
        List<CommentResponseDto> listDto = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                CommentResponseDto commentDto = new CommentResponseDto();
                commentDto.setId(comment.getId());
                commentDto.setText(comment.getText());
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentDto.setCreated(comment.getCreated());
                listDto.add(commentDto);
            }
        }
        return listDto;
    }

}
