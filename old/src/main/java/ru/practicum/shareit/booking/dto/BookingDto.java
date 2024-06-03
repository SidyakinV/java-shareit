package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class BookingDto {

    @NotNull
    private Long itemId;

    @NotBlank
    private String start;

    @NotBlank
    private String end;

    private Long userId;
}
