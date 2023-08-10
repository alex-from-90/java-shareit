package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @MockBean
    private final BookingMapper bookingMapper = new BookingMapper();
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addBooking() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 2L;
        long bookerId = 1L;

        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, new User(ownerId, "test2", "test2@test.com"));
        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, newUser, Status.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        assertThrows(NullPointerException.class, () -> bookingService.addBooking(dto, bookerId));
    }

    @Test
    void testAddBookingItemUnavailable() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        long bookerId = 2L;

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setAvailable(false);
        item.setDescription("A description");
        item.setUser(new User(bookerId, "test", "test@test.com"));
        itemRepository.save(item);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(dto, bookerId));
    }

    @Test
    void addBooking_whenItemIsNotAvailable() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setUser(owner);
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        item.setAvailable(false);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(dto, owner.getId()));
    }

    @Test
    void addBooking_whenEndDateIsBeforeStartDate() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setUser(owner);
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        item.setAvailable(true);
        dto.setEnd(LocalDateTime.now().plusDays(-1));
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(dto, owner.getId()));
    }

    @Test
    void addBooking_whenBookerIdIsInvalid() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setUser(owner);
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(dto, owner.getId());

            given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
            assertThrows(BadRequestException.class, () -> bookingService.addBooking(dto, owner.getId()));
        });
    }

    @Test
    void approveBooking() throws NotFoundException {
        long itemId = 1L;
        long bookerId = 1L;

        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, newUser, Status.WAITING);
        Booking booking = bookingMapper.toBooking(dto, item, newUser);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NullPointerException.class, () -> bookingService.approveBooking(bookerId, true, itemId));
    }

    @Test
    void getBooking() throws NotFoundException {
        long itemId = 1L;
        long bookerId = 1L;

        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, newUser, Status.WAITING);


        Booking booking = bookingMapper.toBooking(dto, item, newUser);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NullPointerException.class, () -> bookingService.getBooking(booking.getId(), bookerId));
    }

    @Test
    public void getBookingWithDifferentBookerAndOwnerIds() {
        long bookingId = 1L;
        long bookerId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User(3L, "test3", "test3@test.com");
        booking.setBooker(booker);
        Item item = new Item();
        item.setId(4L);
        item.setUser(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookingId, bookerId));
    }

    @Test
    void getBookingNotFound() throws NotFoundException {
        long bookerId = 1L;
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, bookerId));
    }

    @Test
    void getAllBookings() throws NotFoundException {
        long itemId = 1L;

        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, newUser, Status.WAITING);
        new PageImpl<>(List.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.ALL, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.PAST, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.FUTURE, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.CURRENT, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.WAITING, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void getAllBookingsNotFoundUser() throws NotFoundException {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1, BookingState.ALL, 0, 10));
    }

    @Test
    void getAllBookingsByItems() throws NotFoundException {
        long itemId = 1L;

        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, newUser, Status.WAITING);
        new PageImpl<>(List.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.ALL, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.PAST, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.FUTURE, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.CURRENT, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.WAITING, 0, 10));
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void getAllBookingsByItemsNotFoundUser() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1, BookingState.REJECTED, 0, 10));
    }

    @Test
    void fullBookingTest() {
        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        new Booking(1, LocalDateTime.MIN, LocalDateTime.now(), item, newUser, Status.WAITING);

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1, BookingState.REJECTED, 0, 10));
    }

    @Test
    void bookingMapperTest() {
        User newUser = new User(1, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.now(), item, newUser, Status.WAITING);
        BookingDto bookingDto = bookingMapper.toFullBookingFromBooking(booking);
        assertEquals(1, bookingDto.getId());
    }
}
