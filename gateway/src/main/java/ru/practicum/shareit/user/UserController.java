package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(
            @Valid @RequestBody UserDto dto
    ) {
        log.info("POST users with dto {}", dto);
        return userClient.addUser(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody UserDto dto,
            @PathVariable Long userId
    ) {
        log.info("PATCH users with dto {}, userId {}", dto, userId);
        return userClient.updateUser(dto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @PathVariable Long userId
    ) {
        log.info("GET user info with id {}", userId);
        return userClient.getUser(userId);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("DELETE user with id {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET users");
        return userClient.getAllUsers();
    }

}
