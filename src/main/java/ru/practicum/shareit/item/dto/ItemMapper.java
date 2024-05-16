package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemResponseDto mapItemToDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(item.getLastBooking());
        dto.setNextBooking(item.getNextBooking());
        dto.setComments(new ArrayList<>());

        List<Comment> comments = item.getComments();
        if (comments != null) {
            for (Comment comment : comments) {
                CommentResponseDto commentDto = new CommentResponseDto();
                commentDto.setId(comment.getId());
                commentDto.setText(comment.getText());
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentDto.setCreated(comment.getCreated());
                dto.getComments().add(commentDto);
            }
        }


        return dto;
    }

    public static Item mapDtoToItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static Item patchItem(Item oldItem, Item newItem) {
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        return newItem;
    }

}
