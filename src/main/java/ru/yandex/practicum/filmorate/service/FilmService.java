package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre_mpa.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre_mpa.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final DbGenreStorage genreStorage;
    private final DbMpaStorage mpaStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (mpaStorage.getById(film.getMpa().getId()) == null) {
            throw new ValidationException("Рейтинг MPA с id: " + mpaStorage.getById(film.getMpa().getId()) + "  не существует");
        }
        if (film.getGenres() != null) {
            genreStorage.genreValidate(film.getGenres());
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public void addLike(Long userId, Long filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        likesStorage.addLike(userId, filmId);
        filmStorage.getFilm(filmId).getIdUserLike().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        likesStorage.deleteLike(filmId, userId);
        filmStorage.getFilm(filmId).getIdUserLike().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getIdPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenre(Long id) {
        return genreStorage.getById(id);
    }

    public List<MPA> getAllRatings() {
        return mpaStorage.getAll();
    }

    public MPA getMpaRating(Long id) {
        if (mpaStorage.getById(id) == null) {
            throw new NotFoundException("Рейтинг MPA с id: " + id + "  не существует");
        }
        return mpaStorage.getById(id);
    }
}
