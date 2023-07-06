package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 * Попробовал  @ResponseStatus
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            log.error("Email не может быть пустым");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        log.info("Запрос на добавление пользователя {}", userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя с id = {}, новые данные: {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getAllUsers() {
        log.info("Запрос на получение списка пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение данных пользователя с  id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Запрос на удаления пользователя с id = {}", userId);
        userService.deleteUserById(userId);
    }
}