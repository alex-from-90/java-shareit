package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto dto, long bookerId);

    BookingDto approveBooking(long bookingId, boolean approved, long bookerId);

    BookingDto getBooking(long bookingId, long bookerId);

    List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state, int from, int size);

    List<BookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state, int from, int size);
}