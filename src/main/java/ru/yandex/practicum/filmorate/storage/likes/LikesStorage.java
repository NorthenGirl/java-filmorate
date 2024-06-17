package ru.yandex.practicum.filmorate.storage.likes;

public interface LikesStorage {
    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);
}
