package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface EnumStorage<T> {
    T getById(Long id);
    List<T> getAll();
}
