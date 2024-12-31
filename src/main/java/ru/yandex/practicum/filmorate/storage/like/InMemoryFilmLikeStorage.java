package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    @Override
    public void addLike(int filmId, int userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).remove(userId);
        }
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        return new HashSet<>(filmLikes.getOrDefault(filmId, Collections.emptySet()));
    }

    @Override
    public int getLikesCount(int filmId) {
        return filmLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}