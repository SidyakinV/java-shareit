package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final JpaBookingRepository bookingRepository;
    private final JpaCommentRepository commentRepository;

    @Override
    public Item addItem(Item item) {
        getUserById(item.getOwnerId());
        Item savedItem = itemRepository.save(item);
        log.info("Добавлена новая вещь: {}", savedItem);
        return savedItem;
    }

    @Override
    public Item updateItem(Item item) {
        getUserById(item.getOwnerId());
        checkNotBlankField(item.getName(), "Название");
        checkNotBlankField(item.getDescription(), "Описание");

        Item oldItem = getItemById(item.getId());
        Item savedItem = itemRepository.save(ItemMapper.patchItem(oldItem, item));
        log.info("Информация о вещи обновлена: {}", savedItem);
        return savedItem;
    }

    @Override
    public Item getItem(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        item.setComments(commentRepository.findByItemId(itemId));
        patchOwnerBookingInfo(item, userId);
        log.info("Получена информация о вещи с id={}: {}", itemId, item);
        return item;
    }

    @Override
    public List<Item> getOwnerItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        items.replaceAll(item -> patchOwnerBookingInfo(item, userId));
        log.info("Получен список вещей пользователя (count: {})", items.size());
        return items;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            log.info("Не определены критерии поиска вещи");
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.searchItems(text.toLowerCase());
        log.info("Сформирован список вещей по фразе '{}'. Найдено совпадений: {}", text, items.size());
        return items;
    }

    @Override
    public Comment addComment(Comment comment) {
        User user = getUserById(comment.getUserId());
        Item item = getItemById(comment.getItemId());
        if (bookingRepository.getUserBookingItems(user.getId(), item.getId(), BookingState.APPROVED).size() < 1) {
            throw new ValidationException(
                    new Violation("error",
                            "Комментарии могут оставлять только те пользователь, которые брали вещь в аренду"));
        }

        comment.setAuthor(user);
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
        bookingInfo.setBookerId(booking.getUserId());
        return bookingInfo;
    }

    private Item patchOwnerBookingInfo(Item item, Long userId) {
        OwnerBookingInfo lastBooking = getBookingInfo(
                bookingRepository.getLastBooking(item.getId(), userId));
        OwnerBookingInfo nextBooking = getBookingInfo(
                bookingRepository.getNextBooking(item.getId(), userId));

        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        return item;
    }

}
