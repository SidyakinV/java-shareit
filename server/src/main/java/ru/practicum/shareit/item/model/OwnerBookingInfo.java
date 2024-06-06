package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OwnerBookingInfo {

    @JsonProperty("id")
    private Long bookingId;

    private Long bookerId;
}
