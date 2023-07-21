package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItemId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public Booking toBooking(BookingDto dto, long bookerId, Status status) {
        return new Booking(dto.getId(), dto.getStart(), dto.getEnd(), dto.getItemId(), bookerId, status);
    }

    public BookingDto toFullBookingFromBooking(Booking booking, Status status) throws NotFoundException {
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + booking.getBookerId()));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id " + booking.getItemId()));

        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItemId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}