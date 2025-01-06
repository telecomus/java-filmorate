package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, MpaDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = mpaStorage.findById(1);
        film.setMpa(mpa);

        Film savedFilm = filmStorage.add(film);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo(film.getName());
    }

    @Test
    void testFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = mpaStorage.findById(1);
        film.setMpa(mpa);

        Film savedFilm = filmStorage.add(film);
        Film foundFilm = filmStorage.findById(savedFilm.getId());

        assertThat(foundFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedFilm);
    }
}