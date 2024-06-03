package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    UserController userController;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    public void addUser() throws Exception {
        UserDto userDto = newUserDto();

        Mockito
                .when(userService.addUser(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userDto = newUserDto();
        userDto.setId(1L);

        Mockito
                .when(userService.updateUser(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getUser() throws Exception {
        User user = newUser(1L);

        Mockito
                .when(userService.getUser(Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/{userId}", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(newUser(1L));
        users.add(newUser(2L));

        Mockito
                .when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(users)));
    }

    private UserDto newUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Пользователь");
        dto.setEmail("user@mail.com");
        return dto;
    }

    private User newUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("Пользователь " + userId);
        user.setEmail(String.format("user%d@mail.ru", userId));
        return user;
    }

}
