package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    static FilmController filmController = new FilmController();

    @Test
    void validateFilmOk() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );

        filmController.validate(film);
    }

    @Test
    void validateNameNullFail() {
        Film film = new Film(
                null,
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );
        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void validateNameIsBlankFail() {
        Film film = new Film(
                "",
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );
        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void validateDescriptionNullOk() {
        Film film = new Film(
                "Фильм",
                null,
                LocalDate.of(2024, 4, 17),
                60
        );
        filmController.validate(film);
    }

    @Test
    void validateDescriptionMaxSizeOk() {
        Film film = new Film(
                "Фильм",
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111",
                LocalDate.of(2024, 4, 17),
                60
        );
        filmController.validate(film);
    }

    @Test
    void validateDescriptionMinSizeOk() {
        Film film = new Film(
                "Фильм",
                "",
                LocalDate.of(2024, 4, 17),
                60
        );
        filmController.validate(film);
    }

    @Test
    void validateDescriptionSizeFail() {
        Film film = new Film(
                "Фильм",
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "111111111111111111111111111111111111111111",
                LocalDate.of(2024, 4, 17),
                60
        );

        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));

        assertEquals("Превышен максимальный размер описания", exception.getMessage());
    }

    @Test
    void validateReleaseDateFail() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(1890, 4, 17),
                60
        );

        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));

        assertEquals("Дата релиза раньше минимальной даты", exception.getMessage());
    }

    @Test
    void validateReleaseDateOk() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(1895, 12, 28),
                60
        );

        filmController.validate(film);
    }

    @Test
    void validateDurationFail() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(2024, 4, 17),
                -1
        );

        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));

        assertEquals("Продолжительность фильма отрицательна", exception.getMessage());
    }

}