package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, ExceptionsHandler.class})
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void approveBooking() throws Exception {
        final BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, request.getId());

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(BookingDto.builder()
                        .id(1)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusHours(1))
                        .booker(new User(1, "test", "test@test.com"))
                        .item(ItemMapper.toItem(itemDto, request, new User(2, "test2", "test2@test.com")))
                        .status(Status.APPROVED)
                        .build());

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        final BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, request.getId());

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(BookingDto.builder()
                        .id(1)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusHours(1))
                        .booker(new User(1, "test", "test@test.com"))
                        .item(ItemMapper.toItem(itemDto, request, new User(2, "test2", "test2@test.com")))
                        .status(Status.WAITING)
                        .build());

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBooking() throws Exception {
        final BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, request.getId());

        when(bookingService.getAllBookingsByBookerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusHours(1))
                        .booker(new User(1, "test", "test@test.com"))
                        .item(ItemMapper.toItem(itemDto, request, new User(2, "test2", "test2@test.com")))
                        .status(Status.WAITING)
                        .build()));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingItems() throws Exception {
        final BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        final ItemRequest request = new ItemRequest(0L, "description", new User(3, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, request.getId());

        when(bookingService.getAllBookingByItemsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusHours(1))
                        .booker(new User(1, "test", "test@test.com"))
                        .item(ItemMapper.toItem(itemDto, request, new User(2, "test2", "test2@test.com")))
                        .status(Status.WAITING)
                        .build()));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingItemsUnsupportedStatus() throws Exception {
        final BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        new ItemDto(1, "test", "testtest", true, 0L);

        when(bookingService.getAllBookingByItemsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(MethodArgumentTypeMismatchException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}