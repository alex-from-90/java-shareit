package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toGetItemDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto dto, long ownerId) throws NotFoundException {
        log.info("Добавлен предмет");
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));
        Item item = toItem(dto, ownerId);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto patchItem(ItemDto dto, long ownerId, long itemId) throws NotFoundException {
        long itemOwnerId = getItemOwnerId(itemId);
        if (itemOwnerId != ownerId) {
            throw new NotFoundException("Обновление невозможно");
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (dto.getName() != null) {
                item.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                item.setDescription(dto.getDescription());
            }
            if (dto.getAvailable() != null) {
                item.setAvailable(dto.getAvailable());
            }
            log.info("Обновлен предмет с id " + itemId);
            return toItemDto(itemRepository.save(item));
        } else {
            throw new NotFoundException("Обновление невозможно");
        }
    }

    public long getItemOwnerId(long itemId) {
        return itemRepository.getReferenceById(itemId).getUser().getId();
    }

    @Override
    public ItemDto getItem(long itemId, long ownerId) throws NotFoundException {
        return itemRepository.findById(itemId)
                .map(item -> {
                    List<Comment> comments = commentRepository.findAllByItemId(itemId);
                    log.info("Получен предмет с id " + itemId);

                    List<Booking> bookings = item.getUser().getId() == ownerId
                            ? Collections.unmodifiableList(bookingRepository.allBookingsForItem(itemId))
                            : Collections.emptyList();

                    return bookings.isEmpty() && item.getUser().getId()== ownerId
                            ? toGetItemDto(item, null, comments)
                            : toGetItemDto(item, bookings, comments);
                })
                .orElseThrow(() -> new NotFoundException("Данный предмет не существует"));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long ownerId) {
        List<ItemDto> allItems =
                itemRepository.findAllByOwnerId(ownerId).stream()
                        .map(l -> ItemMapper.toGetItemDto(l, null, null))
                        .sorted(Comparator.comparing(ItemDto::getId))
                        .collect(Collectors.toList());

        List<Comment> allCommentsByItemsOwner = commentRepository.findAllByItemsOwnerId(ownerId);
        List<Booking> allBookingsByItemsOwner = bookingRepository.findAllByItemsOwnerId(ownerId);

        for (ItemDto item : allItems) {

            List<Comment> comments = allCommentsByItemsOwner
                    .stream()
                    .filter(l -> l.getItemId() == item.getId())
                    .collect(Collectors.toList());
            item.setComments(comments);

            List<Booking> bookings = allBookingsByItemsOwner
                    .stream()
                    .filter(l -> l.getItemId() == item.getId())
                    .collect(Collectors.toList());

            if (bookings.size() != 0) {
                item.setLastBooking(bookings.get(0));
                item.setNextBooking(bookings.get(bookings.size() - 1));
            }
        }
        return allItems;
    }


    @Override
    public List<ItemDto> searchItem(String text, long ownerId) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.search(text).stream()
                    .filter(Item::isAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Comment addComment(Comment dto, long itemId, long authorId) throws BadRequestException {
        LocalDateTime now = LocalDateTime.now();

        if (!bookingRepository.bookingsForItemAndBookerPast(authorId, itemId, now).isEmpty()) {
            User author = userRepository.findById(authorId).orElseThrow(() -> new BadRequestException("Пользователь не найден"));

            Comment comment = new Comment();
            comment.setAuthorId(authorId);
            comment.setItemId(itemId);
            comment.setText(dto.getText());
            comment.setCreated(now);
            comment.setAuthorName(author.getName());

            return commentRepository.save(comment);
        } else {
            throw new BadRequestException("Ошибка запроса");
        }
    }
}