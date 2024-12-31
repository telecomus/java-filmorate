package ru.yandex.practicum.filmorate.storage.like;

import java.util.Set;

public interface FilmLikeStorage {
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    Set<Integer> getLikes(int filmId);
    int getLikesCount(int filmId);
}