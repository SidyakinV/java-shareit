package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapping;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto dto) {
        log.info("Получен POST-запрос на добавление пользователя: {}", dto);
        User user = UserMapping.mapDtoToUser(dto);
        user = userService.addUser(user);
        return UserMapping.mapUserToDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto dto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос на редактирование пользователя: {} (id={})", dto, userId);
        User user = UserMapping.mapDtoToUser(dto);
        user.setId(userId);
        user = userService.updateUser(user);
        return UserMapping.mapUserToDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Получен GET-запрос на получение информации о пользователе: id={}", userId);
        User user = userService.getUser(userId);
        return UserMapping.mapUserToDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос на удаление пользователя: id={}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен GET-запрос на получение списка всех пользователей");
        return userService.getAllUsers().stream()
                .map(UserMapping::mapUserToDto)
                .collect(Collectors.toList());
    }

}
