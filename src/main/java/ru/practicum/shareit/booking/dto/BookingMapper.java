package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public BookingMapper() {
    }

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(BookingDto dto, Item item, User booker, Status status) {
        return new Booking(dto.getId(), dto.getStart(), dto.getEnd(), item, booker, status);
    }

    public BookingDto toFullBookingFromBooking(Booking booking, Status status) throws NotFoundException {
        User booker = booking.getBooker();
        if(booker == null)
            throw new NotFoundException("Не найден пользователь");

        Item item = booking.getItem();
        if(item == null)
            throw new NotFoundException("Не найден предмет");

        return BookingDto.builder()
                .id(booking.getId())
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}