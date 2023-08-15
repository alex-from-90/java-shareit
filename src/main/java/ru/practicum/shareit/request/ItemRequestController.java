package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequest addRequest(@Valid @RequestBody ItemRequestDto request,
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
            @RequestParam(required = false, defaultValue = "0")
            @Min(0) Integer from,
            @Min(1) @RequestParam(required = false, defaultValue = "10")
                    Integer size
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