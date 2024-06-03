package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;
    private String start;
    private String end;
    private String status;
    private BookingUser booker;
    private BookingItem item;
}

