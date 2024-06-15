package ru.yandex.practicum.filmorate.storage.genre_mpa;

import java.util.List;

public interface EnumStorage<T> {
    T getById(Long id);
    List<T> getAll();
}
