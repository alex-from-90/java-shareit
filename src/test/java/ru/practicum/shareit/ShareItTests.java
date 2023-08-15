package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase()
class ShareItTests {

	private final UserRepository userRepository;
	private final UserService userService;

	User user;

	@BeforeEach
	void beforeEach() {
		user = new User(1, "Test", "test@test.com");
	}

	@Test
	void updateUser() throws BadRequestException, NotFoundException {
		userRepository.save(user);

		User newUser = new User(1, "Updated", "test@test.com");
		userService.update(newUser, 1);
		Assertions.assertEquals(userService.getUserById(1), newUser);
	}

	@Test
	void deleteUser() throws BadRequestException {
		userService.add(user);

		userService.delete(1);

		Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1));
	}
}