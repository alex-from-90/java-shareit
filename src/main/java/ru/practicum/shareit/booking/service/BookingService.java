package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface BookingService {
    FullBookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException, NotFoundException;

    FullBookingDto approveBooking(long bookingId, boolean approved, long bookerId)
            throws BadRequestException, NotFoundException;

    FullBookingDto getBooking(long bookingId, long bookerId) throws NotFoundException;

    List<FullBookingDto> getAllBookingsByBookerId(long bookerId, BookingState state) throws NotFoundException;

    List<FullBookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state) throws NotFoundException;
}