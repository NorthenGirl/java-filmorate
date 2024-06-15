package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Long friend_id, Long user_id);

    void deleteFriend(Long friend_id, Long user_id);

    List<User> getFriends(Long user_id);

    List<User> getIdCommonFriends(Long userId, Long commonId);
}
