package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTests {

    @Mock
    private ItemService itemService;

    @InjectMocks
    ItemController itemController;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    public void addItem() throws Exception {
        ItemDto dto = newItemDto(1L);
        Item item = newItem(dto);

        Mockito
                .when(itemService.addItem(Mockito.any(ItemDto.class)))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", dto.getOwnerId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(dto.getRequestId()), Long.class));
    }

    @Test
    public void updateItem() throws Exception {
        ItemDto dto = newItemDto(1L);
        Item item = newItem(dto);

        Mockito
                .when(itemService.updateItem(Mockito.any(ItemDto.class)))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", dto.getOwnerId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(dto.getRequestId()), Long.class));
    }

    @Test
    public void getItem() throws Exception {
        ItemResponseDto dto = newItemResponseDto(1L);

        Mockito
                .when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/items/{itemId}", dto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(dto.getRequestId()), Long.class));
    }

    @Test
    public void getOwnerItems() throws Exception {
        List<ItemResponseDto> items = getItemsResponseDtoList();

        Mockito
                .when(itemService.getOwnerItems(Mockito.anyLong(), Mockito.any()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(items)));
    }

    @Test
    public void searchItems() throws Exception {
        List<ItemResponseDto> items = getItemsResponseDtoList();

        Mockito
                .when(itemService.searchItems(Mockito.anyString(), Mockito.any()))
                .thenReturn(items);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "search_string")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(items)));
    }

    @Test
    public void addComment() throws Exception {
        CommentDto dto = newCommentDto();
        Comment comment = newComment(dto);

        Mockito
                .when(itemService.addComment(Mockito.any(CommentDto.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    private ItemDto newItemDto(Long id) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName("Вещь " + id);
        dto.setDescription("Описание к вещи " + id);
        dto.setAvailable(true);
        dto.setOwnerId(100L + id);
        dto.setRequestId(200L + id);
        return dto;
    }

    private Item newItem(ItemDto dto) {
        User user = new User();
        user.setId(dto.getOwnerId());

        Item item = new Item();
        item.setId(dto.getId());
        item.setOwner(user);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setRequestId(dto.getRequestId());
        return item;
    }

    private ItemResponseDto newItemResponseDto(Long id) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(id);
        dto.setName("Вещь " + id);
        dto.setDescription("Описание к вещи " + id);
        dto.setAvailable(true);
        dto.setRequestId(200L + id);
        return dto;
    }

    private List<ItemResponseDto> getItemsResponseDtoList() {
        List<ItemResponseDto> list = new ArrayList<>();
        list.add(newItemResponseDto(1L));
        list.add(newItemResponseDto(2L));
        return list;
    }

    private CommentDto newCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setText("Текст комментария");
        return dto;
    }


    private Comment newComment(CommentDto dto) {
        User user = new User();
        user.setName("Вася Пупкин");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        return comment;
    }

}
