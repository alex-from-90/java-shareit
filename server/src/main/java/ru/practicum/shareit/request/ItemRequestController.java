package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@SuppressWarnings("unused")
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequest addRequest(@RequestBody ItemRequestDto request,
                                  @RequestHeader(header) long userId) {
        log.info("Получен запрос POST /requests");
        return itemRequestService.addRequest(request, userId);
    }

    @GetMapping
    public List<GetItemRequestDto> getRequests(@RequestHeader(header) long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getRequests(
            @RequestHeader(header) long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Получен запрос GET /requests/all?from=" + from + "&size=" + size);
        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getRequestById(@RequestHeader(header) long userId, @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/" + requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}