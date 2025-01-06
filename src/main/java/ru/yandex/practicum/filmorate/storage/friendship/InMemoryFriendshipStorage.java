package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (friendships.containsKey(userId)) {
            friendships.get(userId).remove(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).remove(userId);
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return new HashSet<>(friendships.getOrDefault(userId, Collections.emptySet()));
    }
}