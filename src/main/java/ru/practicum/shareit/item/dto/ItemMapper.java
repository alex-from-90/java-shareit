package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class ItemMapper {

    public static ItemDto toGetItemDto(Item item, List<BookingForItemDto> bookings, List<CommentDto> comments) {
        ItemDto getItemDto = new ItemDto();
        getItemDto.setId(item.getId());
        getItemDto.setName(item.getName());
        getItemDto.setDescription(item.getDescription());
        getItemDto.setAvailable(item.isAvailable());

        if (bookings != null) {
            LocalDateTime now = LocalDateTime.now();

            getItemDto.setLastBooking(bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .max(Comparator.comparing(BookingForItemDto::getEnd))
                    .orElse(null));

            getItemDto.setNextBooking(bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(BookingForItemDto::getStart))
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
        if (item.getItemRequest() != null)
            dto.setRequestId(item.getItemRequest().getId());
        return dto;
    }

    public static Item toItem(ItemDto dto, ItemRequest request, User user) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setUser(user);//Вещь юзера
        item.setItemRequest(request);
        return item;
    }
}