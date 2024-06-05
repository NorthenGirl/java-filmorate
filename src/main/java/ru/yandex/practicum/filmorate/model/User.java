package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.NotSpase;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @NotSpase
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        if (name == null || name.isBlank()) this.name = login;
        this.birthday = birthday;
    }

    public User(Long id, String email, String login, String name,  LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        if (name == null || name.isBlank()) this.name = login;
        this.birthday = birthday;
    }
}
