package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceIntegrationTest {

    private final UserRepository userRepository;
    private final UserService userService;


    @Test
    public void testAddUser() {
        User user = new User(1L, "John Doe", "john@example.com");
        User addedUser = userService.add(user);

        assertNotNull(addedUser);
        assertEquals(user.getName(), addedUser.getName());
        assertEquals(user.getEmail(), addedUser.getEmail());
    }

    @Test
    public void testGetUserById() throws NotFoundException {
        User user = userRepository.save(new User(1L, "John Doe", "john@example.com"));

        User retrievedUser = userService.getUserById(user.getId());

        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testGetUserById_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(9999L));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User(2L, "John Doe", "john@example.com");
        User user2 = new User(3L, "Jane Smith", "jane@example.com");

        List<User> users = userRepository.saveAll(List.of(user1, user2));
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));

    }

    @Test
    public void testUpdateUser() throws NotFoundException {

        User user = userRepository.save(new User(1L, "John Doe", "john@example.com"));

        User updatedUser = new User(2L, "Updated Name", null);
        User result = userService.update(updatedUser, user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

    }

    @Test
    public void testDeleteUser() {
        User user = userRepository.save(new User(1L, "John Doe", "john@example.com"));

        userService.delete(user.getId());

        assertFalse(userRepository.existsById(user.getId()));
    }
}