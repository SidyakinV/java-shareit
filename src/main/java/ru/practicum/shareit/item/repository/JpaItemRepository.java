package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item as it " +
            "where available = true " +
            "  and (lower(name) like %:text% or lower(description) like %:text%) ")
    List<Item> searchItems(String text);

    List<Item> findByOwnerId(Long userId);
}
