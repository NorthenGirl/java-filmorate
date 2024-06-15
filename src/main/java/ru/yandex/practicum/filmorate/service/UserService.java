package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.DbFriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final DbUserStorage userStorage;
    private final DbFriendsStorage friendsStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Друг с  id = " + friendId + " не найден");
        }
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException("У пользователя с id = " + userId + " не друга с  id = " + friendId);
        }
        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
        return friendsStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long commonId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
        if (userStorage.getUser(commonId) == null) {
            throw new NotFoundException("Пользователь с  commonId = " + commonId + " не найден");
        }
        return friendsStorage.getIdCommonFriends(userId, commonId);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }
}
