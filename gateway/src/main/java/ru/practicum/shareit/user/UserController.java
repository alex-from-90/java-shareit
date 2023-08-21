package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос POST /users");
        return userClient.add(user);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info(String.format("Получен запрос GET /users/%s", id));
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userClient.getAllUsers();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto user, @PathVariable long id) {
        log.info(String.format("Получен запрос PATCH /users/%s", id));
        return userClient.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info(String.format("Получен запрос DELETE /users/%s", id));
        return userClient.delete(id);
    }
}