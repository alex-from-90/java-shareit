package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User add(User user) throws ConflictException {
        try {
            log.info("Добавлен новый User");
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ConflictException("Такой User уже существует");
        }
    }

    public User getUserById(long id) throws NotFoundException {
        Optional<User> userIdDatabase = userRepository.findById(id);
        if (userIdDatabase.isPresent()) {
            log.info("Получен User с id " + id);
            return userIdDatabase.get();
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User update(User user, long id) throws NotFoundException {
        user.setId(id);
        Optional<User> userIdDatabase = userRepository.findById(id);
        if (user.getName() == null) {
            if (userIdDatabase.isPresent()) {
                user.setName(userIdDatabase.get().getName());
            } else {
                throw new NotFoundException("User не найде");
            }
        }
        if (user.getEmail() == null) {
            if (userIdDatabase.isPresent()) {
                user.setEmail(userIdDatabase.get().getEmail());
            } else {
                throw new NotFoundException("User не найден");
            }
        }
        log.info("Обновлен пользователь с id " + id);
        return userRepository.save(user);
    }

    public void delete(long id) {
        log.info("Удалён User с id " + id);
        userRepository.deleteById(id);
    }
}