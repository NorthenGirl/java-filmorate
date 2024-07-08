package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchBy;
import ru.yandex.practicum.filmorate.model.SortedBy;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam("userId") @Positive Long userId,
                                           @RequestParam("friendId") @Positive Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Новый фильм создан");
        return filmService.create(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Фильм обновлен");
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        log.info("Добавлен like пользователя id={} для фильма id={}", userId, id);
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удален like пользователя id={} c фильма id={}", userId, id);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") @Min(1) Integer count,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "year", required = false) @Min(1895) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByQuery(@RequestParam String query,
                                      @RequestParam Set<String> by) {
        query = "%" + query + "%";
        if (by.size() < 2) {
            if (by.contains("title")) {
                return filmService.searchFilms(SearchBy.Title, query);
            }
            if (by.contains("director")) {
                return filmService.searchFilms(SearchBy.Director, query);
            }
        }
        if (by.size() == 2 && by.contains("director") && by.contains("title")) {
            return filmService.searchFilms(SearchBy.DirectorAndTitle, query);
        }
        throw new ValidationException("Ошибка дополнительных параметров");
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable Long directorId, @RequestParam(value = "sortBy") String sortBy) {
        if ("likes".equals(sortBy)) {
            return filmService.getFilmsByDirectorId(SortedBy.Likes, directorId);
        } else if ("year".equals(sortBy)) {
            return filmService.getFilmsByDirectorId(SortedBy.Years, directorId);
        }
        throw new ValidationException("Ошибка переменной сортирования");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        log.info("Фильм с id {} удален", id);
    }

}
