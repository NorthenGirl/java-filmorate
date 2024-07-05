package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    List<Film> getCommonFilms(Long userId, Long friendId);

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film getFilm(Long id);

    List<Film> getIdPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getFilmsByDirectorIdSortedByLikes(Long directorId);

    List<Film> getFilmsByDirectorIdSortedByYear(Long directorId);
}
