package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Long friend_id, Long user_id);
    void deleteFriend(Long friend_id, Long user_id);
    List<Long> getIdFriends(Long user_id);
    List<Long> getIdCommonFriends(Long userId, Long commonId);

    interface FilmStorage {

        List<Film> findAll();

        Film create(Film film);

        Film update(Film film);

        void delete(Long id);

        Film getFilm(Long id);
    }
}
