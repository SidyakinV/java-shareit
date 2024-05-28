package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.OwnerBookingInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemJsonTests {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testJsonItemDto() throws JsonProcessingException {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Вещь");
        dto.setDescription("Описание вещи");
        dto.setAvailable(true);
        dto.setOwnerId(2L);
        dto.setRequestId(3L);

        String expected = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"available\":%s,\"ownerId\":%d,\"requestId\":%d}",
                dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), dto.getOwnerId(), dto.getRequestId());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test
    public void testJsonItemResponseDto() throws JsonProcessingException {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(1L);
        dto.setName("Вещь необыкновенная");
        dto.setDescription("Описание вещи");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        OwnerBookingInfo lastBooking = new OwnerBookingInfo();
        lastBooking.setBookerId(3L);
        lastBooking.setBookingId(4L);
        dto.setLastBooking(lastBooking);

        OwnerBookingInfo nextBooking = new OwnerBookingInfo();
        nextBooking.setBookerId(5L);
        nextBooking.setBookingId(6L);
        dto.setNextBooking(nextBooking);

        List<CommentResponseDto> comments = new ArrayList<>();
        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(7L);
        comment.setText("Комментарий");
        comment.setAuthorName("Вася Пупкин");
        comment.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        comments.add(comment);
        dto.setComments(comments);

        String expected = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"available\":%s,\"lastBooking\":" +
                        "{\"bookerId\":%d,\"id\":%d},\"nextBooking\":" +
                        "{\"bookerId\":%d,\"id\":%d},\"comments\":" +
                        "[{\"id\":%d,\"text\":\"%s\",\"authorName\":\"%s\",\"created\":\"%s\"}]," +
                        //"null," +
                        "\"requestId\":%d}",
                dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(),
                dto.getLastBooking().getBookerId(), dto.getLastBooking().getBookingId(),
                dto.getNextBooking().getBookerId(), dto.getNextBooking().getBookingId(),

                dto.getComments().get(0).getId(),
                dto.getComments().get(0).getText(),
                dto.getComments().get(0).getAuthorName(),
                dto.getComments().get(0).getCreated(),
                dto.getRequestId());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test
    public void testJsonCommentDto() throws JsonProcessingException {
        CommentDto dto = new CommentDto();
        dto.setItemId(1L);
        dto.setUserId(2L);
        dto.setText("Комментарий");

        String expected = String.format("{\"text\":\"%s\",\"itemId\":%d,\"userId\":%d}",
                dto.getText(), dto.getItemId(), dto.getUserId());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test
    public void testJsonCommentResponseDto() throws JsonProcessingException {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(1L);
        dto.setText("Комментарий");
        dto.setAuthorName("Вася Пупкин");
        dto.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String expected = String.format(
                "{\"id\":%d,\"text\":\"%s\",\"authorName\":\"%s\",\"created\":\"%s\"}",
                dto.getId(), dto.getText(), dto.getAuthorName(), dto.getCreated());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

}
