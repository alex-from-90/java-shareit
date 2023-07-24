package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final BookingMapper bookingMapper;

    public ItemDto toGetItemDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDto getItemDto = new ItemDto();
        getItemDto.setId(item.getId());
        getItemDto.setName(item.getName());
        getItemDto.setDescription(item.getDescription());
        getItemDto.setAvailable(item.isAvailable());

        if (bookings != null) {
            LocalDateTime now = LocalDateTime.now();

            getItemDto.setLastBooking(bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .min(Comparator.comparing(Booking::getEnd))
                .map(booking -> bookingMapper.toFullBookingFromBooking(booking,booking.getStatus()))
                    .orElse(null));

            getItemDto.setNextBooking(bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(Booking::getStart))
                    .map(booking -> bookingMapper.toFullBookingFromBooking(booking,booking.getStatus()))
                    .orElse(null));
        }

        getItemDto.setComments(comments);
        return getItemDto;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public Item toItem(ItemDto dto, User user) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setUser(user);  // Устанавливаем пользователя
        return item;
    }
}