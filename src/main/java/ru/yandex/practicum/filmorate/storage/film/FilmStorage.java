package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film getFilm(Long id);

    List<Film> getIdPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    public List<Film> getFilmsByDirectorIdSortedByLikes(Long directorId);

    List<Film> getFilmsByDirectorIdSortedByYear(Long directorId);

    public List<Film> getRecommendationsForUser(Long userId);
}
