package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendshipDbStorage.class, UserDbStorage.class})
class FriendshipDbStorageTest {
    private final FriendshipDbStorage friendshipStorage;
    private final UserDbStorage userStorage;

    @Test
    public void testAddFriend() {

        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser1 = userStorage.add(user1);
        User savedUser2 = userStorage.add(user2);

        friendshipStorage.addFriend(savedUser1.getId(), savedUser2.getId());

        Set<Integer> user1Friends = friendshipStorage.getFriends(savedUser1.getId());
        assertThat(user1Friends)
                .isNotEmpty()
                .contains(savedUser2.getId());

        Set<Integer> user2Friends = friendshipStorage.getFriends(savedUser2.getId());
        assertThat(user2Friends)
                .isEmpty();
    }

    @Test
    public void testRemoveFriend() {

        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser1 = userStorage.add(user1);
        User savedUser2 = userStorage.add(user2);

        friendshipStorage.addFriend(savedUser1.getId(), savedUser2.getId());

        friendshipStorage.removeFriend(savedUser1.getId(), savedUser2.getId());

        Set<Integer> friends = friendshipStorage.getFriends(savedUser1.getId());
        assertThat(friends)
                .isEmpty();
    }

    @Test
    public void testGetFriends() {

        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser1 = userStorage.add(user1);
        User savedUser2 = userStorage.add(user2);
        User savedUser3 = userStorage.add(user3);

        friendshipStorage.addFriend(savedUser1.getId(), savedUser2.getId());
        friendshipStorage.addFriend(savedUser1.getId(), savedUser3.getId());

        Set<Integer> friends = friendshipStorage.getFriends(savedUser1.getId());
        assertThat(friends)
                .hasSize(2)
                .contains(savedUser2.getId(), savedUser3.getId());
    }

    @Test
    public void testGetFriendsForUserWithNoFriends() {

        User user = new User();
        user.setEmail("user@test.com");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userStorage.add(user);

        Set<Integer> friends = friendshipStorage.getFriends(savedUser.getId());
        assertThat(friends)
                .isEmpty();
    }
}