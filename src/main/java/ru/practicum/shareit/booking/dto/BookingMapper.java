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

    public BookingDto toFullBookingFromBooking(Booking booking, Status status) throws NotFoundException {
        User booker = booking.getBooker();
        Item item = booking.getItem();

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