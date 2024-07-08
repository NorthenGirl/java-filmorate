package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public Collection<Film> getRecommendationsForUser(Long userId) {
        throwIfUserNotFound(userId);
        return filmStorage.getRecommendationsForUser(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        throwIfUserNotFound(userId);
        throwIfUserNotFound(friendId);
        friendsStorage.addFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        throwIfUserNotFound(userId);
        throwIfUserNotFound(friendId);
        friendsStorage.deleteFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public List<User> getFriends(Long userId) {
        throwIfUserNotFound(userId);
        return friendsStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long commonId) {
        throwIfUserNotFound(userId);
        throwIfUserNotFound(commonId);
        return friendsStorage.getIdCommonFriends(userId, commonId);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public long deleteUser(long id) {
        return userStorage.delete(id);
    }

    private void throwIfUserNotFound(Long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
    }
}