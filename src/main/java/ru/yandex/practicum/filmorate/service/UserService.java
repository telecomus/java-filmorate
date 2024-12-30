package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public User getUser(int id) {
        return userStorage.findById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);

        if (friendships.containsKey(userId)) {
            friendships.get(userId).remove(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).remove(userId);
        }
    }

    public List<User> getFriends(int userId) {
        userStorage.findById(userId);
        Set<Integer> userFriends = friendships.getOrDefault(userId, Collections.emptySet());
        return userFriends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = friendships.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherFriends = friendships.getOrDefault(otherId, Collections.emptySet());

        Set<Integer> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherFriends);

        return commonFriendIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}