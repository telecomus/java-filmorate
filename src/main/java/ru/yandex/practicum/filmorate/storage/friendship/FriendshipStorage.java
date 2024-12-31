package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Set;

public interface FriendshipStorage {
    void addFriend(int userId, int friendId);
    void removeFriend(int userId, int friendId);
    Set<Integer> getFriends(int userId);
}