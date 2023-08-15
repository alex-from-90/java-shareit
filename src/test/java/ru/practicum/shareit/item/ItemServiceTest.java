package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository,
                new BookingMapper()
        );
    }

    @Test
    void addItem() throws NotFoundException {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        User user = new User(1L, "test", "test@test.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(ItemMapper.toItem(itemDto, null, user));

        assertEquals(itemDto.getName(), itemService.addItem(itemDto, 1).getName());
    }

    @Test
    void addItemNoUser() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0L);
        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, itemId));
    }

    @Test
    void patchItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, new User(ownerId, "test", "test@test.com"));
        when(itemRepository.getReferenceById(itemId))
                .thenReturn(item);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemDto, itemService.patchItem(itemDto, ownerId, itemId));
    }

    @Test
    void patchItemNotFound() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 2L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));

        assertThrows(NullPointerException.class, () -> itemService.patchItem(itemDto, ownerId, itemId));
    }

    @Test
    void patchItemWithIdOnly() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        ItemDto newDto = new ItemDto(itemId, null, null, null, 0L);
        when(itemRepository.getReferenceById(itemId))
                .thenReturn(item);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemDto, itemService.patchItem(newDto, ownerId, itemId));
    }

    @Test
    void bookingsIsNotEmptyGetItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        User user = new User(ownerId, "test", "test@test.com");
        Item item = ItemMapper.toItem(itemDto, null, user);
        ItemDto getItemDto = ItemMapper.toGetItemDto(item, Collections.emptyList(), Collections.emptyList());

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setStatus(Status.WAITING);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.allBookingsForItem(anyLong()))
                .thenReturn(List.of(booking));

        assertNotEquals(getItemDto, itemService.getItem(itemId, ownerId));
    }

    @Test
    void getItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        ItemDto getItemDto = ItemMapper.toGetItemDto(item, Collections.emptyList(), Collections.emptyList());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.allBookingsForItem(anyLong()))
                .thenReturn(Collections.emptyList());

        assertEquals(getItemDto, itemService.getItem(itemId, ownerId));
    }

    @Test
    void getItemNotFound() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        ItemMapper.toGetItemDto(item, Collections.emptyList(), Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, ownerId));
    }

    @Test
    void getAllItemsByOwner() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        ItemDto getItemDto = ItemMapper.toGetItemDto(item, Collections.emptyList(), Collections.emptyList());

        when(itemRepository.findAllByOwnerId(any(), any())).thenReturn(List.of(item));
        when(commentRepository.findAllByItemsOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemsOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());

        assertEquals(List.of(getItemDto), itemService.getAllItemsByOwner(ownerId, 0, 10));
    }

    @Test
    void searchItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));

        when(itemRepository.search(anyString(), any())).thenReturn(List.of(item));

        assertEquals(List.of(itemDto), itemService.searchItem("test", ownerId, 0, 10));
    }

    @Test
    void searchItemEmptyText() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0L);

        assertEquals(List.of(), itemService.searchItem("", ownerId, 0, 10));
    }

    @Test
    void addComment() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthor(new User(ownerId, "test", "test@test.com"));
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), item, new User(ownerId, "test", "test@test.com"), Status.APPROVED);
        when(bookingRepository.bookingsForItemAndBookerPast(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1, "test", "test@test.com")));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertEquals(comment.getText(), itemService.addComment(comment, itemId, 1).getText());
    }

    @Test
    void addCommentNoBooking() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(ownerId, "test", "test@test.com"));
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthor(new User(ownerId, "test", "test@test.com"));
        new Booking(1, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), item, new User(ownerId, "test", "test@test.com"), Status.APPROVED);
        when(bookingRepository.bookingsForItemAndBookerPast(anyLong(), anyLong(), any()))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.addComment(comment, itemId, 1));
    }
}