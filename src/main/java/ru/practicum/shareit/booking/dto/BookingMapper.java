package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getItemId(), booking.getStart(), booking.getEnd());
    }

    public static Booking toBooking(BookingDto dto, long bookerId, Status status) {
        return new Booking(dto.getId(), dto.getStart(), dto.getEnd(), dto.getItemId(), bookerId, status);
    }

    public static FullBookingDto toFullBookingFromBooking(Booking booking, Status status,
                                                          ItemRepository itemRepository,
                                                          UserRepository userRepository) throws NotFoundException {
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Не найден User с id " + booking.getBookerId()));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id " + booking.getItemId()));

        return new FullBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), item, booker, status);
    }
}