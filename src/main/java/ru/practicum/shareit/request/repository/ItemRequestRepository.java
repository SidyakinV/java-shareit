package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByUserOrderByCreatedDesc(User user);

    Slice<ItemRequest> findByUserNotOrderByCreatedDesc(User user, Pageable pageable);

    Optional<ItemRequest> findItemRequestById(Long id);
}
