package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User update(User user, Long userId) throws BadRequestException;

    Collection<User> getAll();

    void deleteById(Long userId) throws BadRequestException;

    User getById(long id) throws NotFoundException;
}