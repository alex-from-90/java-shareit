package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
public class ItemServiceIntegrationTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    public void setup() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
    }

    @Test
    public void testAddRequest_ExistingUser_ReturnsItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        long userId = 1;
        User user = new User();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(new ItemRequest());

        ItemRequest result = itemRequestService.addRequest(requestDto, userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testAddRequest_NonExistingUser_ThrowsNotFoundException() {
        ItemRequestDto requestDto = new ItemRequestDto();
        long userId = 1;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(requestDto, userId));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testGetRequests_ExistingUser_ReturnsListOfGetItemRequestDto() {
        long userId = 1;
        new User();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findAllByRequesterId(userId)).thenReturn(new ArrayList<>());
        Mockito.when(itemRequestRepository.findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created")))
                .thenReturn(new ArrayList<>());

        List<GetItemRequestDto> result = itemRequestService.getRequests(userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created"));
    }

    @Test
    public void testGetRequests_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        Mockito.when(userService.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(userId));
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequesterId(Mockito.anyLong());
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAllByRequesterId(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    public void testGetRequestsPageable_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        Integer from = 0;
        Integer size = 10;
        Mockito.when(userService.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(userId, from, size));
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequesterIdIsNot(Mockito.anyLong());
    }

    @Test
    public void testGetRequestById_ExistingUserAndExistingRequest_ReturnsGetItemRequestDto() {
        long userId = 1;
        long requestId = 1;
        new User();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        ItemRequest request = new ItemRequest();
        request.setItems(Collections.emptyList());
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        GetItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(requestId);
    }

    @Test
    public void testGetRequestById_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        long requestId = 1;
        Mockito.when(userService.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequestId(Mockito.anyLong());
    }

    @Test
    public void testGetRequestById_ExistingUserAndNonExistingRequest_ThrowsNotFoundException() {
        long userId = 1;
        long requestId = 1;
        new User();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }
}