package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    ItemRequestServiceImpl requestService;

    @Test
    public void testSetDescription() {
        ItemRequest itemRequest = new ItemRequest();
        String description = "test description";
        itemRequest.setDescription(description);
        assertEquals(description, itemRequest.getDescription());
    }

    @Test
    void addRequest() {
        long userId = 1L;
        User newUser = new User(1, "test", "test@test.com");
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("test");
        ItemRequest request = ItemRequestMapper.toItemRequest(dto, newUser);
        request.setItems(Collections.emptyList());
        when(itemRequestRepository.save(any()))
                .thenReturn(request);
        assertEquals(request, requestService.addRequest(dto, userId));
    }

    @Test
    void getRequests() {
        long userId = 1L;
        long itemId = 1L;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("test");
        User newUser = new User(1, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toItemRequest(dto, newUser);
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        request.setItems(Collections.singletonList(item));
        GetItemRequestDto getItemRequestDto = ItemRequestMapper.toGetItemRequestDto(request);
        when(itemRequestRepository.findAllByRequesterId(anyLong(), any()))
                .thenReturn(List.of(request));
        when(userService.existsById(anyLong()))
                .thenReturn(true);
        assertEquals(getItemRequestDto.getDescription(), requestService.getRequests(userId).get(0).getDescription());
    }

    @Test
    void getRequestsPageable() {
        long userId = 1L;
        long itemId = 1L;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("test");
        User newUser = new User(1, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toItemRequest(dto, newUser);
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        request.setItems(Collections.singletonList(item));
        GetItemRequestDto getItemRequestDto = ItemRequestMapper.toGetItemRequestDto(request);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdIsNot(anyLong(), any())).thenReturn(List.of(request));
        assertEquals(getItemRequestDto.getDescription(),
                requestService.getRequests(userId, 0, 10).get(0).getDescription());
    }

    @Test
    void getRequestById() {
        long userId = 1L;
        long itemId = 1L;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("test");
        User newUser = new User(1, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toItemRequest(dto, newUser);
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemMapper.toItem(itemDto, request, newUser);
        request.setItems(Collections.singletonList(item));
        GetItemRequestDto getItemRequestDto = ItemRequestMapper.toGetItemRequestDto(request);
        when(userService.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        assertEquals(getItemRequestDto.getDescription(), requestService.getRequestById(userId, 1).getDescription());
    }
}