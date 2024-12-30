package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
class FilmControllerTest {
    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private FilmService filmService;
    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        openMocks(this);
        filmController = new FilmController(filmService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
    void shouldNotCreateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
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
}