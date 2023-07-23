package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto dto, @RequestHeader("X-Sharer-User-Id") long ownerId)
            throws NotFoundException {
        log.info("Получен запрос POST /items");
        return itemService.addItem(dto, ownerId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto dto, @PathVariable long itemId,
                             @RequestHeader("X-Sharer-User-Id") long ownerId) throws NotFoundException {
        log.info(String.format("Получен запрос PATCH /items/%s", itemId));
        return itemService.patchItem(dto, ownerId, itemId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long ownerId) throws NotFoundException {
        log.info(String.format("Получен запрос GET /items/%s", itemId));
        return itemService.getItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос GET /items");
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info(String.format("Получен запрос GET /items/search?text=%s", text));
        return itemService.searchItem(text.toLowerCase(), ownerId);
    }

    @PostMapping("{itemId}/comment")
    public Comment addComment(@RequestBody @Valid Comment dto, @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long authorId)
            throws BadRequestException {
        log.info("Получен запрос POST /items/" + itemId + "/comment");
        return itemService.addComment(dto, itemId, authorId);
    }
}