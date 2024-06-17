package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FrendshipStorage {
    Friendship getById(Long id);

    List<Friendship> getAll();
}
