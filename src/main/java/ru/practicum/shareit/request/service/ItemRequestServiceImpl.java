package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.RequestWithAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addItemRequest(ItemRequestDto dto) {
        User user = getUserById(dto.getUserId());
        ItemRequest itemRequest = ItemRequestMapper.mapDtoToRequest(dto);
        itemRequest.setUser(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(itemRequest);
        log.info("Создан новый запрос вещи: {}", savedRequest);
        return savedRequest;
    }

    @Override
    public List<RequestWithAnswerDto> getOwnItemRequests(Long userId) {
        User user = getUserById(userId);
        List<ItemRequest> requests = requestRepository.findByUserOrderByCreatedDesc(user);
        log.info("Получен список запросов вещей, количество записей: {}", requests.size());
        return getRequestWithAnswerDtoList(requests);
    }

    @Override
    public List<RequestWithAnswerDto> getAllItemRequests(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        List<ItemRequest> requests = requestRepository.findByUserNotOrderByCreatedDesc(user, pageable).getContent();
        log.info("Получен список запросов вещей, количество записей: {}", requests.size());
        return getRequestWithAnswerDtoList(requests);
    }

    @Override
    public RequestWithAnswerDto getItemRequest(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest itemRequest = requestRepository.findItemRequestById(requestId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("itemRequest", String.format("Не найден запрос вещи с id=%d", requestId))
                ));
        return ItemRequestMapper.mapRequestWithAnswerDto(itemRequest, getItemsByRequest(requestId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("user", String.format("Пользователь с id=%d не найден!", userId))));
    }

    private List<ItemResponseDto> getItemsByRequest(Long requestId) {
        List<Item> items = itemRepository.findByRequestId(requestId);
        return items.stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    private List<RequestWithAnswerDto> getRequestWithAnswerDtoList (List<ItemRequest> requests) {
        return requests.stream()
                .map(itemRequest ->
                        ItemRequestMapper.mapRequestWithAnswerDto(itemRequest, getItemsByRequest(itemRequest.getId())))
                .collect(Collectors.toList());
    }

}
