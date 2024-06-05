package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.DbLikesStorage;
import ru.yandex.practicum.filmorate.storage.DbMpaRatingStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final DbFilmStorage dbFilmStorage;
    private final DbLikesStorage likesStorage;
    private final DbGenreStorage genreStorage;
    private final DbMpaRatingStorage mpaRatingStorage;

    public Collection<Film> findAll() {
        return  dbFilmStorage.findAll();
    }

    public Film create(Film film) {
     /* if (mpaRatingStorage.getById(film.getMpa().getId()) == null) {
            throw new ValidationException("Указанный MPA  не найден");
        }
       /* if (film.getGenres() != null) {
            dbFilmStorage.createValidate(film.getGenres());
        }*/
        return dbFilmStorage.create(film);
    }

    public Film update(Film film) {
        return dbFilmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        likesStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likesStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return likesStorage.getIdPopularFilms(count)
                .stream()
                .map(id -> dbFilmStorage.getFilm(id))
                .collect(Collectors.toList());
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenre(Long id) {
        return genreStorage.getById(id);
    }

    public List<MPA> getAllRatings() {
        return mpaRatingStorage.getAll();
    }

    public MPA getMpaRating(Long id) {
        return mpaRatingStorage.getById(id);
    }
}
