package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ResponseItemDto mapItemToDto(Item item) {
        ResponseItemDto dto = new ResponseItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(item.getLastBooking());
        dto.setNextBooking(item.getNextBooking());

        List<ResponseCommentDto> comments = new ArrayList<>();
        for (Comment comment : item.getComments()) {
            ResponseCommentDto commentDto = new ResponseCommentDto();
            commentDto.setId(comment.getId());
            commentDto.setText(comment.getText());
            commentDto.setAuthorName(comment.getAuthor().getName());
            commentDto.setCreated(comment.getCreated());
            comments.add(commentDto);
        }
        dto.setComments(comments);

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
