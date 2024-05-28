package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestJsonTests {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testJsonItemRequestDto() throws JsonProcessingException {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Запрос вещи");
        dto.setUserId(1L);

        String expected = String.format(
                "{\"description\":\"%s\",\"userId\":%d}",
                dto.getDescription(), dto.getUserId());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test
    public void testJsonItemRequestResponseDto() throws JsonProcessingException {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(1L);
        dto.setDescription("Запрос вещи");
        dto.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String expected = String.format(
                "{\"id\":%d,\"description\":\"%s\",\"created\":\"%s\"}",
                dto.getId(), dto.getDescription(), dto.getCreated());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

    @Test
    public void testJsonRequestWithAnswerDto() throws JsonProcessingException {
        RequestWithAnswerDto dto = new RequestWithAnswerDto();
        dto.setId(1L);
        dto.setDescription("Описание запрашиваемой вещи");
        dto.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));


        String expected = String.format(
                "{\"id\":%d,\"description\":\"%s\",\"created\":\"%s\",\"items\":null}",
                dto.getId(), dto.getDescription(), dto.getCreated());

        assertEquals(expected, mapper.writeValueAsString(dto));
    }

}
