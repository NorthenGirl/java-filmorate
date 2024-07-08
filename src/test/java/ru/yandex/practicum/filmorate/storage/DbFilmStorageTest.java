package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmStorage.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
public class DbFilmStorageTest {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final DirectorStorage directorStorage;


    @Test
    @DisplayName("get common films for 2 users")
    @Sql(scripts = {"/test-get-common.sql"})
    void getCommonFilmsTest() {
        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L);
        assertEquals(2, commonFilms.size());
        assertTrue(commonFilms.stream().anyMatch(f -> f.getId() == 3));
        assertTrue(commonFilms.stream().anyMatch(f -> f.getId() == 4));
    }

    @Test
    @Sql(scripts = {"/test-get-enums.sql"})
    void createFilm() {
        filmStorage.create(new Film(
                "film_name",
                "description",
                LocalDate.of(2024, 4, 17),
                200,
                MPA.builder().id(1L).name("G").build()
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
                MPA.builder().id(3L).name("PG").build(),
                new ArrayList<>(),
                new ArrayList<>()
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
