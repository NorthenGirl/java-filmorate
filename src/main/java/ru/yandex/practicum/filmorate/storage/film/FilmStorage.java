package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film getFilm(Long id);

    List<Film> getIdPopularFilms(Integer count);
}
