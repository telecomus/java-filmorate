package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
                       MpaDbStorage mpaStorage,
                       GenreDbStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateReleaseDate(film);
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());
        return filmStorage.update(film);
    }

    public Film getFilm(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public void addLike(int filmId, int userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        Film film = filmStorage.findById(filmId);
        filmLikeStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        Film film = filmStorage.findById(filmId);
        filmLikeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = filmLikeStorage.getLikesCount(f1.getId());
                    int likes2 = filmLikeStorage.getLikesCount(f2.getId());
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateMpa(Mpa mpa) {
        try {
            mpaStorage.findById(mpa.getId());
        } catch (NotFoundException e) {
            throw new ValidationException("MPA рейтинг с ID " + mpa.getId() + " не найден");
        }
    }

    private void validateGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        // Получаем все ID жанров для проверки
        Set<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        // Получаем все существующие жанры одним запросом
        List<Genre> existingGenres = genreStorage.findByIds(genreIds);
        Set<Integer> existingGenreIds = existingGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        // Проверяем, все ли жанры существуют
        if (existingGenreIds.size() != genreIds.size()) {
            Set<Integer> invalidIds = new HashSet<>(genreIds);
            invalidIds.removeAll(existingGenreIds);
            throw new ValidationException("Жанры с ID " + invalidIds + " не найдены");
        }
    }

    public boolean isFilmExists(int id) {
        return filmStorage.existsById(id);
    }
}