package ru.yandex.practicum.filmorate.storage.likes;

public interface LikesStorage {
    void addLike(Long film_id, Long user_id);

    void deleteLike(Long film_id, Long user_id);



}
