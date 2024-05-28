package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
//@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым!")
    private String name;

    @NotBlank(message = "Email пользователя не может быть пустым!")
    @Email(message = "Некорректный email")
    private String email;
}
