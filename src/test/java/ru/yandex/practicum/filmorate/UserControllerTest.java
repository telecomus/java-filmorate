package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
class UserControllerTest {
    @Mock
    private UserService userService;
    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        openMocks(this);
        userController = new UserController(userService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        when(userService.addUser(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getLogin().contains(" ")) {
                throw new ValidationException("Логин не может содержать пробелы");
            }
            return user;
        });
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setName(null);
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);
        assertNotNull(createdUser);
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("log in");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}