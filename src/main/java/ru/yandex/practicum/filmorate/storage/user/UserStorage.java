package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User create(User user);

    User update(User user);

    void delete(Long id);

    User getUser(Long id);
}
