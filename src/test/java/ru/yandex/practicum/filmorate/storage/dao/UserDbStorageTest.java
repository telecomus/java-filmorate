package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userStorage.add(user);

        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userStorage.add(user);
        User foundUser = userStorage.findById(savedUser.getId());

        assertThat(foundUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }
}