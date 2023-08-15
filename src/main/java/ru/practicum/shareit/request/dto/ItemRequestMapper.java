package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto, User requester) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static GetItemRequestDto toGetItemRequestDto(ItemRequest request) {
        return GetItemRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .items(request.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                .description(request.getDescription())
                .build();
    }
}