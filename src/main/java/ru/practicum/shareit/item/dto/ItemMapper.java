package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ItemMapper {

    public static ItemDto toGetItemDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDto getItemDto = new ItemDto();
        getItemDto.setId(item.getId());
        getItemDto.setName(item.getName());
        getItemDto.setDescription(item.getDescription());
        getItemDto.setAvailable(item.isAvailable());

        if (bookings != null) {
            LocalDateTime now = LocalDateTime.now();

            getItemDto.setLastBooking(bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null));

            getItemDto.setNextBooking(bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null));
        }

        getItemDto.setComments(comments);
        return getItemDto;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public static Item toItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        //TODO

        item.setUser(null);
        return item;
    }
}