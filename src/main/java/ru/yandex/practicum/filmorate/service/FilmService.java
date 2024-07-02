package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.director.DbDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre_mpa.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre_mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private final DbDirectorStorage directorStorage;

    public Collection<Film> getCommonFilms(Long userId,Long friendId){
        return filmStorage.getCommonFilms(userId,friendId);
    }


    public Collection<Film> findAll() {
        Set<Long> ids = filmStorage.findAll().stream().map(Film::getId).collect(Collectors.toSet());
        ArrayList<Film> films = new ArrayList<>();
        ids.stream()
                .forEach(id -> films.add(filmStorage.getFilm(id)));
        return films;
    }

    public Film create(Film film) {
        mpaStorage.mpaValidateBadRequest(film.getMpa().getId());
        film.getMpa().setName(mpaStorage.getById(film.getMpa().getId()).getName());
        if (!film.getGenres().isEmpty()) {
            genreStorage.genreValidate(film.getGenres());
            film.getGenres().stream()
                    .forEach(genre -> genre.setName(genreStorage.getById(genre.getId()).getName()));
        }
        if (!film.getDirectors().isEmpty()) {
            directorStorage.directorValidate(film.getDirectors());
            film.getDirectors().stream()
                    .forEach(director -> director.setName(directorStorage.getById(director.getId()).getName()));
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        mpaStorage.mpaValidateNotFound(film.getMpa().getId());
        film.getMpa().setName(mpaStorage.getById(film.getMpa().getId()).getName());
        if (!film.getGenres().isEmpty()) {
            genreStorage.genreValidate(film.getGenres());
            Set<Long> ids = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            ArrayList<Genre> genres = new ArrayList<>();
            ids.stream()
                    .forEach(id -> genres.add(genreStorage.getById(id)));
            film.setGenres(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            directorStorage.directorValidate(film.getDirectors());
            film.getDirectors().stream()
                    .forEach(director -> director.setName(directorStorage.getById(director.getId()).getName()));
        }

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
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
//        likesStorage.deleteLike(filmId, userId);
        likesStorage.deleteLike(userId, filmId);
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
        mpaStorage.mpaValidateNotFound(id);
        return mpaStorage.getById(id);
    }

    public List<Film> getFilmsByDirectorIdSortedByLikes(Long directorId) {
        if (directorStorage.getById(directorId) == null) {
            throw new NotFoundException("Режиссер с id " + directorId + " не найден");
        }
        return filmStorage.getFilmsByDirectorIdSortedByLikes(directorId);
    }

    public List<Film> getFilmsByDirectorIdSortedByYear(Long directorId) {
        if (directorStorage.getById(directorId) == null) {
            throw new NotFoundException("Режиссер с id " + directorId + " не найден");
        }
        return filmStorage.getFilmsByDirectorIdSortedByYear(directorId);
    }
}
