package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    static UserController userController = new UserController();

    @Test
    void validateUserOk() {
        final User user = new User(
                "user@mail.ru",
                "login",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        userController.validate(user);
    }

    @Test
    void validateEmailWithoutDogFail() {
        final User user = new User(
                "user-mail.ru",
                "login",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Не верный формат email", exception.getMessage());
    }

    @Test
    void validateEmailNullFail() {
        final User user = new User(
                null,
                "login",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void validateEmailIsBlankFail() {
        final User user = new User(
                " ",
                "login",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void validateLoginNullFail() {
        final User user = new User(
                "user@mail.ru",
                null,
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void validateLoginIsBlankFail() {
        final User user = new User(
                "user@mail.ru",
                " ",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void validateLoginContainsSpaceFail() {
        final User user = new User(
                "user@mail.ru",
                "Login Fail",
                "name",
                LocalDate.of(2014, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void validateNameNullOk() {
        final User user = new User(
                "user@mail.ru",
                "Login",
                null,
                LocalDate.of(2014, 4, 17)
        );
        userController.validate(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void validateNameIsBlankOk() {
        final User user = new User(
                "user@mail.ru",
                "Login",
                "",
                LocalDate.of(2014, 4, 17)
        );
        userController.validate(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void validateBirthdayAfterNowFail() {
        final User user = new User(
                "user@mail.ru",
                "Login",
                "name",
                LocalDate.of(2025, 4, 17)
        );
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));

        assertEquals("Дата рождения должна быть раньше текущей", exception.getMessage());
    }

    @Test
    void validateBirthdayNowOk() {
        final User user = new User(
                "user@mail.ru",
                "Login",
                "name",
                LocalDate.now()
        );
        userController.validate(user);
    }

    @Test
    void validateBirthdayNullOk() {
        final User user = new User(
                "user@mail.ru",
                "Login",
                "name",
                null
        );
        userController.validate(user);
    }


}