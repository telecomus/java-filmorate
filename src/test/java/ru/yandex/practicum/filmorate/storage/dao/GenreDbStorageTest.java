package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void testFindAll() {
        List<Genre> genres = genreStorage.findAll();
        assertThat(genres).isNotEmpty();
    }

    @Test
    void testFindById() {
        Genre genre = genreStorage.findById(1);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
    }
}