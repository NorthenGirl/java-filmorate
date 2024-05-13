package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private  final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;


    public Set<Long> addLike(Long filmId, Long userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmStorage.getFilms().get(filmId).getIdUserLike().add(userId);
        return filmStorage.getFilms().get(filmId).getIdUserLike();
    }

    public Set<Long> deleteLike(Long filmId, Long userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmStorage.getFilms().get(filmId).getIdUserLike().remove(userId);
        return filmStorage.getFilms().get(filmId).getIdUserLike();
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> Long.compare(f2.getIdUserLike().size(), f1.getIdUserLike().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
