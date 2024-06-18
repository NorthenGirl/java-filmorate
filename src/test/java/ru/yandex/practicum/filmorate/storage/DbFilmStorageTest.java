package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmStorage.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
public class DbFilmStorageTest {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    @Test
    @Sql(scripts = {"/test-get-enums.sql"})
    void createFilm() {
        filmStorage.create(new Film(
                "film_name",
                "description",
                LocalDate.of(2024, 4, 17),
                200,
                new MPA(1L, "G")
        ));

        Film film = filmStorage.getFilm(1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "film_name");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 200);
        assertThat(film).hasFieldOrProperty("mpa");
    }

    @Test
    @Sql(scripts = {"/test-get-enums.sql", "/tests-get-films.sql"})
    void updateFilm() {

        filmStorage.update(new Film(
                2L,
                "new_film_name1",
                "new_description1",
                LocalDate.of(2024, 4, 17),
                90,
                null,
                new MPA(3L, "PG"),
                null
        ));

        Film film = filmStorage.getFilm(2L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "new_film_name1");
        assertThat(film).hasFieldOrPropertyWithValue("description", "new_description1");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 90);
    }

    @Test
    @Sql(scripts = {"/test-get-enums.sql", "/tests-get-films.sql"})
    void getFilm() {
        Film film = filmStorage.getFilm(1L);

        assertThat(film).hasFieldOrPropertyWithValue("name", "film_name1");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description1");
        assertThat(film).hasFieldOrProperty("releaseDate");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 90);
        assertThat(film).hasFieldOrProperty("mpa");
    }

    @Test
    @Sql(scripts = {"/test-get-enums.sql", "/tests-get-films.sql"})
    void delete() {
        filmStorage.delete(1L);

        assertEquals(1, filmStorage.findAll().size());
    }

    @Test
    @Sql(scripts = {"/test-get-enums.sql", "/tests-get-films.sql"})
    void findAll() {
        List<Film> films = filmStorage.findAll();

        assertEquals(2, films.size());

        assertThat(films.get(0)).hasFieldOrPropertyWithValue("name", "film_name1");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("description", "description1");
        assertThat(films.get(0)).hasFieldOrProperty("releaseDate");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("duration", 90);
        assertThat(films.get(1)).hasFieldOrProperty("mpa");

        assertThat(films.get(1)).hasFieldOrPropertyWithValue("name", "film_name2");
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("description", "description2");
        assertThat(films.get(1)).hasFieldOrProperty("releaseDate");
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("duration", 60);
        assertThat(films.get(1)).hasFieldOrProperty("mpa");
    }
}
