package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface LikesStorage {
    void addLike(Long film_id, Long user_id);
    void deleteLike(Long film_id, Long user_id);
    List<Long> getIdPopularFilms(Integer count);

    interface UserStorage {

        List<User> findAll();

        User create(User user);

        User update(User user);

        void delete(Long id);

        User getUser(Long id);
    }
}
