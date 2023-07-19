package ru.practicum.shareit.user.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public User add(User user) {
        validateUserEmail(user.getEmail(), null);
        Long userId = getNextId();
        log.info("Добавлен пользователь. userId = {}, user = {}", userId, user);
        user.setId(userId);
        usersMap.put(userId, user);
        return user;
    }

    @Override
    public User update(User user, Long userId) throws BadRequestException {
        validateUserExists(userId);
        validateUserEmail(user.getEmail(), userId);
        User userUpdate = usersMap.get(userId);
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        log.info("User id = {} обновлён", userId);
        return userUpdate;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public void deleteById(Long userId) throws BadRequestException {
        validateUserExists(userId);
        usersMap.remove(userId);
        log.info("user id = {} удалён", userId);
    }

    @Override
    public User getById(long id) throws NotFoundException {
        User user = usersMap.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    private void validateUserEmail(String email, Long id) {
        boolean emailExists = usersMap.values().stream()
                .filter(u -> !Objects.equals(u.getId(), id))
                .anyMatch(u -> Objects.equals(email, u.getEmail()));

        if (emailExists) {
            throw new ConflictException("Ошибка обновления. Такая почта уже существует");
        }
    }

    private void validateUserExists(long id) throws BadRequestException {
        if (!usersMap.containsKey(id)) {
            throw new BadRequestException("Ошибка обновления пользователя. Id пользователя не найден");
        }
    }
}