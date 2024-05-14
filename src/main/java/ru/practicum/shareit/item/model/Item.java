package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Transient
    private OwnerBookingInfo lastBooking;

    @Transient
    private OwnerBookingInfo nextBooking;

    @Transient
    private List<Comment> comments;
}
