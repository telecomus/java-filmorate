package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public Film getFilm(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.findById(filmId);
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.findById(filmId);
        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).remove(userId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size();
                    int likes2 = filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size();
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
}