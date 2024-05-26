package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    Map<Long, User> usersList = new HashMap<>();
    Map<Long, Item> itemsList = new HashMap<>();

    public ItemServiceTest() {
        usersList.put(1L, newUser(1L));
        usersList.put(2L, newUser(2L));

        itemsList.put(1L, newItem(1L, usersList.get(1L)));
        itemsList.put(2L, newItem(2L, usersList.get(2L)));
    }

    @BeforeEach
    public void init() {
        Mockito
                .lenient()
                .when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    User user = usersList.get(userId);
                    return user != null ? Optional.of(user) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long itemId = invocationOnMock.getArgument(0, Long.class);
                    Item item = itemsList.get(itemId);
                    return item != null ? Optional.of(item) : Optional.empty();
                });
        Mockito
                .lenient()
                .when(bookingRepository.getFinishedUserBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    return userId == 1 ? new Booking() : null;
                });
        Mockito
                .lenient()
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]
                );
        Mockito
                .lenient()
                .when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]
                );
    }

    @Test
    public void addItem_success() {
        ItemDto dto = newItemDto();
        dto.setOwnerId(1L);

        Item item = itemService.addItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void addItem_fail_userNotFound() {
        ItemDto dto = newItemDto();
        dto.setOwnerId(99L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void updateItem_success() {
        ItemDto dto = newItemDto();
        dto.setId(1L);
        dto.setOwnerId(1L);

        Item item = itemService.updateItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void updateItem_success_withEmptyName() {
        ItemDto dto = newItemDto();
        dto.setId(1L);
        dto.setOwnerId(1L);
        dto.setName(null);

        Item item = itemService.updateItem(dto);

        assertEquals(itemsList.get(1L).getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void updateItem_success_withEmptyDescription() {
        ItemDto dto = newItemDto();
        dto.setId(1L);
        dto.setOwnerId(1L);
        dto.setDescription(null);

        Item item = itemService.updateItem(dto);

        assertEquals(dto.getName(), item.getName());
        assertEquals(itemsList.get(1L).getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    public void updateItem_fail_userNotFound() {
        ItemDto dto = newItemDto();
        dto.setId(1L);
        dto.setOwnerId(99L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void updateItem_fail_itemNotFound() {
        ItemDto dto = newItemDto();
        dto.setId(99L);
        dto.setOwnerId(1L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void updateItem_fail_blankName() {
        ItemDto dto = newItemDto();
        dto.setId(99L);
        dto.setOwnerId(1L);
        dto.setName("  ");

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> itemService.updateItem(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void updateItem_fail_blankDescription() {
        ItemDto dto = newItemDto();
        dto.setId(99L);
        dto.setOwnerId(1L);
        dto.setDescription("  ");

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> itemService.updateItem(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addComment_success() {
        CommentDto dto = newCommentDto();
        dto.setItemId(1L);
        dto.setUserId(1L);

        Comment comment = itemService.addComment(dto);

        assertEquals(dto.getText(), comment.getText());
    }

    @Test
    public void addComment_fail_itemNotFound() {
        CommentDto dto = newCommentDto();
        dto.setItemId(99L);
        dto.setUserId(1L);

        final Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addComment_fail_userNotFound() {
        CommentDto dto = newCommentDto();
        dto.setItemId(1L);
        dto.setUserId(99L);

        final Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(dto)
        );
        assertNotNull(exception);
    }

    @Test
    public void addComment_fail_withoutBooking() {
        CommentDto dto = newCommentDto();
        dto.setItemId(1L);
        dto.setUserId(2L);

        final Exception exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(dto)
        );
        assertNotNull(exception);
    }

    private ItemDto newItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("Название вещи");
        dto.setDescription("Описание вещи");
        dto.setAvailable(true);
        return dto;
    }

    private User newUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("User" + userId);
        return user;
    }

    private Item newItem(Long itemId, User owner) {
        Item item = new Item();
        item.setId(itemId);
        item.setName("Item" + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private CommentDto newCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setText("Какой-то комментарий");
        return dto;
    }

}
