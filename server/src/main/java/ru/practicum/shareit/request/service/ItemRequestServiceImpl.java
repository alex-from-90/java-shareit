package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetPageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest addRequest(ItemRequestDto request, long userId) {
        User requester = userService.getUserById(userId);
        return itemRequestRepository.save(ItemRequestMapper.toItemRequest(request, requester));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemRequestDto> getRequests(long userId) {
        if (!userService.existsById(userId))
            throw new NotFoundException("Не удалось вернуть ответы");
        return itemRequestRepository.findAllByRequesterId(
                        userId,
                        Sort.by(Sort.Direction.ASC, "created")
                ).stream()
                .map(ItemRequestMapper::toGetItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemRequestDto> getRequests(long userId, Integer from, Integer size) {
        if (!userService.existsById(userId)) {
            throw new NotFoundException("Данный ответ не существует");
        }
        return itemRequestRepository.findAllByRequesterIdIsNot(userId, new OffsetPageable(from, size, Sort.by(Sort.Direction.ASC, "created")))
                .stream()
                .map(ItemRequestMapper::toGetItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GetItemRequestDto getRequestById(long userId, long requestId) {
        if (!userService.existsById(userId)) {
            throw new NotFoundException("Данный ответ не существует");
        }
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return ItemRequestMapper.toGetItemRequestDto(request);
    }
}