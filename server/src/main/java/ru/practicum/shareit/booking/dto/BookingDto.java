package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingDto {

    private Long itemId;
    private String start;
    private String end;
    private Long userId;
}
