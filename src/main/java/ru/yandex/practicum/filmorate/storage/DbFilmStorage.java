package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class DbFilmStorage implements FriendsStorage.FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("select * from films", DbFilmStorage::makeFilm);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, releaseDate, duration, rating_id) values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey().longValue());
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery =
                "update films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "delete from films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film getFilm(Long id) {
        final String sqlQuery = "select * from film WHERE film_id = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, DbFilmStorage::makeFilm, id);
        if (films.size() != 1) {
            throw new NotFoundException("film id=" + id);
        }
        return films.get(0);
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
        rs.getLong("film_id"),
        rs.getString("name"),
        rs.getString("description"),
        rs.getDate("releaseDate").toLocalDate(),
        rs.getInt("duration"),
        new MPA(rs.getLong("mpa")));
    }


    public void createValidate(List<Genre> genres) {
        for (Genre genre : genres) {
            if ((jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class), genre.getGenre_id())).isEmpty()) {
                throw new ValidationException("Жанр с id = " + genre.getGenre_id() + " отсутствует");
            }
        }
    }
}

