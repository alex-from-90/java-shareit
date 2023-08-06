package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForItemDto {
    private long id;
    private Item item;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
