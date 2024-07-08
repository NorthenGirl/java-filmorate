package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreEnum;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.SearchBy;
import ru.yandex.practicum.filmorate.model.SortedBy;
import ru.yandex.practicum.filmorate.storage.director.DbDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private final DbDirectorStorage directorStorage;
    private final EventService eventService;

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }


    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        mpaStorage.mpaValidateBadRequest(film.getMpa().getId());
        film.getMpa().setName(mpaStorage.getById(film.getMpa().getId()).getName());
        setGenreToFilm(film);

        if (!film.getDirectors().isEmpty()) {
            directorStorage.directorValidate(film.getDirectors());
            film.getDirectors().forEach(director -> director.setName(directorStorage.getById(director.getId()).getName()));
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        mpaStorage.mpaValidateNotFound(film.getMpa().getId());
        film.getMpa().setName(mpaStorage.getById(film.getMpa().getId()).getName());
        setGenreToFilm(film);

        if (!film.getDirectors().isEmpty()) {
            directorStorage.directorValidate(film.getDirectors());
            film.getDirectors()
                    .forEach(director -> director.setName(directorStorage.getById(director.getId()).getName()));
        }
        return filmStorage.update(film);
    }

    private void setGenreToFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            Set<Long> genreIds = new HashSet<>();
            film.getGenres().forEach(g -> genreIds.add(g.getId()));
            List<Genre> genres = genreIds
                    .stream()
                    .map(id -> Genre.builder()
                            .id(id)
                            .name(GenreEnum.getNameByDBId(id))
                            .build())
                    .toList();
            film.setGenres(genres);
        }
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
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        likesStorage.deleteLike(userId, filmId);
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return filmStorage.getIdPopularFilms(count, genreId, year);
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

    public List<Film> getFilmsByDirectorId(SortedBy sortedBy, Long directorId) {
        if (directorStorage.getById(directorId) == null) {
            throw new NotFoundException("Режиссер с id " + directorId + " не найден");
        }
        switch (sortedBy) {
            case Likes -> {
                return filmStorage.getFilmsByDirectorIdSortedByLikes(directorId);
            }
            case Years -> {
                return filmStorage.getFilmsByDirectorIdSortedByYear(directorId);
            }
        }
        throw new ValidationException("Ошибка переменной сортировки");
    }

    public void deleteFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        filmStorage.delete(id);
    }

    public List<Film> searchFilms(SearchBy by, String query) {
        switch (by) {
            case Title -> {
                return filmStorage.getFilmsByTitle(query);
            }
            case Director -> {
                return filmStorage.getFilmsByDirector(query);
            }
            case DirectorAndTitle -> {
                return filmStorage.getFilmsByDirectorAndTitle(query);
            }
            default -> throw new ValidationException("Ошибка дополнительных параметров");
        }
    }
}
