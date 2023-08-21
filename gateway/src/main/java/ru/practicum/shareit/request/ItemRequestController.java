package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@AllArgsConstructor
@SuppressWarnings("unused")
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @Valid @RequestBody ItemRequestDto request,
            @RequestHeader(header) long userId
    ) {
        log.info("Получен запрос POST /requests");
        return itemRequestClient.addRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(
            @RequestHeader(header) long userId
    ) {
        log.info("Получен запрос GET /requests");
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
            @RequestHeader(header) long userId,
            @RequestParam(required = false, defaultValue = "0")
            @Min(0) Integer from,
            @Min(1) @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Получен запрос GET /requests/all?from=" + from + "&size=" + size);
        return itemRequestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(header) long userId, @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/" + requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}