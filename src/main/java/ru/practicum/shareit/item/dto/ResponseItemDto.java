package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.OwnerBookingInfo;

import java.util.List;

@Data
public class ResponseItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private OwnerBookingInfo lastBooking;
    private OwnerBookingInfo nextBooking;
    private List<ResponseCommentDto> comments;
}
