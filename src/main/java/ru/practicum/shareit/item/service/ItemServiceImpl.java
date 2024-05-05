package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item addItem(Item item) {
        checkUserExists(item.getOwnerId());
        Item savedItem = itemRepository.addItem(item);
        log.info("Добавлена новая вещь: {}", savedItem);
        return savedItem;
    }

    @Override
    public Item updateItem(Item item) {
        checkUserExists(item.getOwnerId());
        checkNotBlankField(item.getName(), "Название");
        checkNotBlankField(item.getDescription(), "Описание");

        Item savedItem = itemRepository.updateItem(item);
        checkItemExists(savedItem, item.getId());
        log.info("Информация о вещи обновлена: {}", savedItem);

        return savedItem;
    }

    @Override
    public Item getItem(Long itemId) {
        Item item = itemRepository.getItem(itemId);
        checkItemExists(item, itemId);
        log.info("Получена информация о вещи с id={}: {}", itemId, item);
        return item;
    }

    @Override
    public List<Item> getOwnerItems(Long userId) {
        List<Item> items = itemRepository.getOwnerItems(userId);
        log.info("Получен список вещей пользователя (count: {})", items.size());
        return items;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> items = itemRepository.searchItems(text);
        log.info("Сформирован список вещей по фразе '{}'. Найдено совпадений: {}", text, items.size());
        return items;
    }

    private void checkUserExists(Long userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new NotFoundException(
                    new Violation("user", String.format("Пользователь с id=%d не найден!", userId)));
        }
    }

    private void checkItemExists(Item item, Long itemId) {
        if (item == null) {
            throw new NotFoundException(
                    new Violation("item", String.format("Вещь с id=%d не найдена!", itemId)));
        }
    }

    private void checkNotBlankField(String value, String name) {
        if (value != null && value.isBlank()) {
            throw new ValidationException(
                    new Violation(name, name + " вещи не может быть пустым!"));
        }
    }

}
