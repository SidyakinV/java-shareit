package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @BeforeEach
    public void init() {
        Mockito
                .lenient()
                .when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    return userId == 1 ? Optional.of(new User()) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]
                );
    }

    @Test
    public void addItemRequest_success() {
        ItemRequestDto dto = newItemRequestDto();
        dto.setUserId(1L);

        ItemRequest request = requestService.addItemRequest(dto);

        assertEquals(dto.getDescription(), request.getDescription());
    }

    @Test
    public void addItemRequest_fail_userNotFound() {
        ItemRequestDto dto = newItemRequestDto();
        dto.setUserId(99L);

        final Exception exception = assertThrows(
                NotFoundException.class,
                () -> requestService.addItemRequest(dto)
        );
        assertNotNull(exception);
    }

    private ItemRequestDto newItemRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Вещь необыкновенная");
        return dto;
    }

}
