package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
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

    public Booking toBooking(BookingDto dto, Item item, User booker) {
        return new Booking(dto.getId(), dto.getStart(), dto.getEnd(), item, booker, dto.getStatus());
    }

    public BookingDto toFullBookingFromBooking(Booking booking) {
        User booker = booking.getBooker();
        Item item = booking.getItem();
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }
}