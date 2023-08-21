package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@SuppressWarnings("unused")
@AllArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestBody @Valid ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.info("Получен запрос POST /items");
        return itemClient.addItem(dto, ownerId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> patchItem(
            @RequestBody ItemDto dto,
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.info(String.format("Получен запрос PATCH /items/%s", itemId));
        return itemClient.patchItem(dto, ownerId, itemId);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.info(String.format("Получен запрос GET /items/%s", itemId));
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
            @RequestParam(defaultValue = "10", required = false) @Min(1) int size
    ) {
        log.info("Получен запрос GET /items");
        return itemClient.getAllItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
            @RequestParam(defaultValue = "10", required = false) @Min(1) int size
    ) {
        log.info(String.format("Получен запрос GET /items/search?text=%s", text));
        return itemClient.searchItem(text.toLowerCase(), ownerId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestBody @Valid CommentDto dto,
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long authorId
    ) {
        log.info("Получен запрос POST /items/" + itemId + "/comment");
        return itemClient.addComment(dto, itemId, authorId);
    }
}