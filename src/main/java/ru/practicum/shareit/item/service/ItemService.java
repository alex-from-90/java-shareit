package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto dto, long ownerId);

    ItemDto patchItem(ItemDto dto, long ownerId, long itemId);

    ItemDto getItem(long itemId, long ownerId);

    List<ItemDto> getAllItemsByOwner(long ownerId, int from, int size);

    List<ItemDto> searchItem(String text, long ownerId, int from, int size);

    Comment addComment(Comment dto, long itemId, long authorId);
}