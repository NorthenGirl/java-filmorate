package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    static FilmController filmController = new FilmController();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
    }

    @Test
    void validateFilmOk() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );
        validator.validate(film);
    }

    @Test
    void validateNameNullFail() {
        Film film = new Film(
                null,
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("name", "не должно быть пустым");

        assertEquals(1, violations.size());
        assertEquals(expectedViolation.propertyPath, violations.get(0).getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.get(0).getMessage());
    }

    @Test
    void validateNameIsBlankFail() {
        Film film = new Film(
                "",
                "Описание",
                LocalDate.of(2024, 4, 17),
                60
        );
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("name", "не должно быть пустым");

        assertEquals(1, violations.size());
        assertEquals(expectedViolation.propertyPath, violations.get(0).getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.get(0).getMessage());
    }

    @Test
    void validateDescriptionNullOk() {
        Film film = new Film(
                "Фильм",
                null,
                LocalDate.of(2024, 4, 17),
                60
        );
        validator.validate(film);
    }

    @Test
    void validateDescriptionMaxSizeOk() {
        Film film = new Film(
                "Фильм",
                "1".repeat(200),
                LocalDate.of(2024, 4, 17),
                60
        );
        validator.validate(film);
    }

    @Test
    void validateDescriptionMinSizeOk() {
        Film film = new Film(
                "Фильм",
                "",
                LocalDate.of(2024, 4, 17),
                60
        );
        validator.validate(film);
    }

    @Test
    void validateDescriptionSizeFail() {
        Film film = new Film(
                "Фильм",
                "1".repeat(205),
                LocalDate.of(2024, 4, 17),
                60
        );

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("description",
                "размер должен находиться в диапазоне от 0 до 200");

        assertEquals(1, violations.size());
        assertEquals(expectedViolation.propertyPath, violations.get(0).getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.get(0).getMessage());
    }

    @Test
    void validateReleaseDateFail() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(1890, 4, 17),
                60
        );

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("releaseDate",
                "дожна быть раньше 28.12.1985");

        assertEquals(1, violations.size());
        assertEquals(expectedViolation.propertyPath, violations.get(0).getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.get(0).getMessage());
    }

    @Test
    void validateReleaseDateOk() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(1895, 12, 28),
                60
        );

        validator.validate(film);
    }

    @Test
    void validateDurationFail() {
        Film film = new Film(
                "Фильм",
                "Описание",
                LocalDate.of(2024, 4, 17),
                -1
        );
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("duration",
                "должно быть больше 0");

        assertEquals(1, violations.size());
        assertEquals(expectedViolation.propertyPath, violations.get(0).getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.get(0).getMessage());
    }
}