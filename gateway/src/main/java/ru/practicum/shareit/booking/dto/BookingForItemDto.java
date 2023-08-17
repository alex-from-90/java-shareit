package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForItemDto {
    private long id;
    private ItemDto item;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
}
