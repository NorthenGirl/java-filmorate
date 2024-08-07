package ru.yandex.practicum.filmorate.storage.genre_mpa;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getById(Long id);

    List<Genre> getAll();

    void genreValidate(List<Genre> genres);

}
