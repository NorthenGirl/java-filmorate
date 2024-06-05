package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final InMemoryUserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Set<Long> addFriend(Long userId, Long friendId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Друг с id " + friendId + " не найден");
        }
        userStorage.getUsers().get(userId).getFriendsId().add(friendId);
        userStorage.getUsers().get(friendId).getFriendsId().add(userId);
        return userStorage.getUsers().get(userId).getFriendsId();
    }

    public Set<Long> deleteFriend(Long userId, Long friendId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Друг с id " + userId + " не найден");
        }
        userStorage.getUsers().get(userId).getFriendsId().remove(friendId);
        userStorage.getUsers().get(friendId).getFriendsId().remove(userId);
        return userStorage.getUsers().get(userId).getFriendsId();
    }

    public List<User> getFriends(Long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return userStorage.getUsers().get(userId).getFriendsId()
                .stream()
                .map(id -> userStorage.getUsers().get(id))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long commonId) {
        return userStorage.getUsers().get(userId).getFriendsId()
                .stream()
                .filter(id -> userStorage.getUsers().get(commonId).getFriendsId().contains(id))
                .map(id -> userStorage.getUsers().get(id))
                .collect(Collectors.toList());
    }
}
