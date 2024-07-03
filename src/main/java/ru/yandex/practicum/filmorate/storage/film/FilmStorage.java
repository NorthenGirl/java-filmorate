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

    List<Film> getFilmsByDirectorIdSortedByLikes(Long directorId);

    List<Film> getFilmsByDirectorIdSortedByYear(Long directorId);

    List<Film> getFilmsByDirectorAndTitle(String query);

    List<Film> getFilmsByDirector(String query);

    List<Film> getFilmsByTitle(String query);
}
