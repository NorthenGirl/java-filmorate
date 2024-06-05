package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFriendsStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final DbUserStorage dbUserStorage;
    private final DbFriendsStorage friendsStorage;

    public Collection<User> findAll() {
        return dbUserStorage.findAll();
    }

    public User create(User user) {
        return dbUserStorage.create(user);
    }

    public User update(User user) {
        return dbUserStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        friendsStorage.deleteFriend(userId, friendId);

    }

    public List<User> getFriends(Long userId) {
        return friendsStorage.getIdFriends(userId)
                .stream()
                .map(id -> dbUserStorage.getUser(id))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long commonId) {
        return friendsStorage.getIdCommonFriends(userId, commonId)
                .stream()
                .map(id -> dbUserStorage.getUser(id))
                .collect(Collectors.toList());
    }

    public User getUser(Long id) {
        return dbUserStorage.getUser(id);
    }
}
