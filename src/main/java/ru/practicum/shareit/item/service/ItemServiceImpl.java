package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OwnerBookingInfo;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final JpaBookingRepository bookingRepository;
    private final JpaCommentRepository commentRepository;

    @Override
    public Item addItem(ItemDto dto) {
        Item item = ItemMapper.mapDtoToItem(dto);
        item.setOwner(getUserById(dto.getOwnerId()));

        Item savedItem = itemRepository.save(item);
        log.info("Добавлена новая вещь: {}", savedItem);
        return savedItem;
    }

    @Override
    public Item updateItem(ItemDto dto) {
        Item item = ItemMapper.mapDtoToItem(dto);
        item.setOwner(getUserById(dto.getOwnerId()));

        checkNotBlankField(item.getName(), "Название");
        checkNotBlankField(item.getDescription(), "Описание");

        Item oldItem = getItemById(item.getId());
        Item savedItem = itemRepository.save(ItemMapper.patchItem(oldItem, item));
        log.info("Информация о вещи обновлена: {}", savedItem);
        return savedItem;
    }

    @Override
    public ItemResponseDto getItem(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        log.info("Получена информация о вещи с id={}: {}", itemId, item);

        ItemResponseDto dto = ItemMapper.mapItemToDto(item);

        List<Comment> comments = commentRepository.findByItemId(itemId);
        dto.setComments(CommentMapper.mapCommentsToListDto(comments));

        patchOwnerBookingInfo(dto, userId);

        return dto;
    }

    @Override
    public List<ItemResponseDto> getOwnerItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        log.info("Получен список вещей пользователя (count: {})", items.size());

        return items.stream()
                .map(ItemMapper::mapItemToDto)
                .map(item -> patchOwnerBookingInfo(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text) {
        if (text.isEmpty()) {
            log.info("Не определены критерии поиска вещи");
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.searchItems(text.toLowerCase());
        log.info("Сформирован список вещей по фразе '{}'. Найдено совпадений: {}", text, items.size());

        return items.stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Comment addComment(CommentDto dto) {
        Comment comment = CommentMapper.mapDtoToComment(dto);
        comment.setItem(getItemById(dto.getItemId()));
        comment.setAuthor(getUserById(dto.getUserId()));

        if (bookingRepository.getFinishedUserBooking(dto.getUserId(), dto.getItemId()) == null) {
            throw new ValidationException(
                    new Violation("error",
                            "Комментарии могут оставлять только те пользователь, которые брали вещь в аренду"));
        }

        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        log.info("Комментарий добавлен: {}", savedComment);
        return savedComment;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("user", String.format("Пользователь с id=%d не найден!", userId))));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("item", String.format("Вещь с id=%d не найдена!", itemId))));
    }

    private void checkNotBlankField(String value, String name) {
        if (value != null && value.isBlank()) {
            throw new ValidationException(
                    new Violation(name, name + " вещи не может быть пустым!"));
        }
    }

    private OwnerBookingInfo getBookingInfo(Booking booking) {
        if (booking == null) {
            return null;
        }
        OwnerBookingInfo bookingInfo = new OwnerBookingInfo();
        bookingInfo.setBookingId(booking.getId());
        bookingInfo.setBookerId(booking.getUser().getId());
        return bookingInfo;
    }

    private ItemResponseDto patchOwnerBookingInfo(ItemResponseDto dto, Long userId) {
        OwnerBookingInfo lastBooking = getBookingInfo(
                bookingRepository.getLastBooking(dto.getId(), userId));
        OwnerBookingInfo nextBooking = getBookingInfo(
                bookingRepository.getNextBooking(dto.getId(), userId));

        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        return dto;
    }

}
