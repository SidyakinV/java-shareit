package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE available = true " +
            "  AND (LOWER(name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "       OR LOWER(description) LIKE LOWER(CONCAT('%', :text, '%'))) ")
    Slice<Item> searchItems(String text, Pageable pageable);

    Slice<Item> findByOwnerId(Long userId, Pageable pageable);

    List<Item> findByRequestId(Long requestId);
}
