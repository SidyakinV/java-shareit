package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTests {

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    ItemRequestController requestController;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
    }

    @Test
    public void addItemRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Описание запрашиваемой вещи");
        requestDto.setUserId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());

        Mockito
                .when(requestService.addItemRequest(Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void getOwnItemRequests() throws Exception {
        List<RequestWithAnswerDto> list = getAnswerList();

        Mockito
                .when(requestService.getOwnItemRequests(Mockito.anyLong()))
                .thenReturn(list);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    public void getAllItemRequests() throws Exception {
        List<RequestWithAnswerDto> list = getAnswerList();

        Mockito
                .when(requestService.getAllItemRequests(Mockito.anyLong(), Mockito.any()))
                .thenReturn(list);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    public void getItemRequest() throws Exception {
        RequestWithAnswerDto dto = newRequestWithAnswer(1L);

        Mockito
                .when(requestService.getItemRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated())));
    }

    private RequestWithAnswerDto newRequestWithAnswer(Long id) {
        RequestWithAnswerDto dto = new RequestWithAnswerDto();
        dto.setId(id);
        dto.setDescription("Описание " + id);
        dto.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    private List<RequestWithAnswerDto> getAnswerList() {
        List<RequestWithAnswerDto> list = new ArrayList<>();
        list.add(newRequestWithAnswer(1L));
        list.add(newRequestWithAnswer(2L));
        return list;
    }

}
