package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    List<User> findAll();

    User findById(int id);

    void delete(int id);
}