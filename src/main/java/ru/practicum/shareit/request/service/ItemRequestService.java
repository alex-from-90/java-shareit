package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequestDto request, long userId);

    List<GetItemRequestDto> getRequests(long userId);

    List<GetItemRequestDto> getRequests(long userId, Integer from, Integer size);

    GetItemRequestDto getRequestById(long userId, long requestId);
}