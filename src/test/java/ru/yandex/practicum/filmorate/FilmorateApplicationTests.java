package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
	private FilmController filmController;
	private UserController userController;
	private Validator validator;

	@BeforeEach
	void setUp() {
		filmController = new FilmController();
		userController = new UserController();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldNotCreateFilmWithEmptyName() {
		Film film = new Film();
		film.setName("");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(100);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty());
	}

	@Test
	void shouldNotCreateFilmWithReleaseDateBefore1895() {
		Film film = new Film();
		film.setName("Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(1895, 12, 27));
		film.setDuration(100);

		assertThrows(ValidationException.class, () -> filmController.create(film));
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
		user.setBirthday(LocalDate.of(2000, 1, 1));

		User createdUser = userController.create(user);
		assertEquals(user.getLogin(), createdUser.getName());
	}

	@Test
	void shouldNotCreateFilmWithLongDescription() {
		Film film = new Film();
		film.setName("Name");
		film.setDescription("A".repeat(201)); // создаём строку длиной 201 символ
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(100);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty());
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
	void shouldNotCreateFilmWithNegativeDuration() {
		Film film = new Film();
		film.setName("Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(-1);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty());
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