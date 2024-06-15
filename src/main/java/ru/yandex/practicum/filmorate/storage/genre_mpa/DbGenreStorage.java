package ru.yandex.practicum.filmorate.storage.genre_mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@AllArgsConstructor
public class DbGenreStorage implements EnumStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getById(Long id) {
        try {
            String sqlQuery = "SELECT * FROM genres WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Жанра с id: " + id + " не существует");
        }
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(Genre.class));
    }

    public Genre getFromFilm(Long film_id) {
        try {
            String sqlQuery = "SELECT * FROM genres WHERE id IN (SELECT genre_id FROM films WHERE film_id = ?)";
            return jdbcTemplate.queryForObject(sqlQuery, new DataClassRowMapper<>(Genre.class), film_id);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public boolean genreValidate(List<Genre> genres) {
        for (Genre genre : genres) {
            if ((jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class), genre.getId())).isEmpty()) {
                throw new ValidationException("Жанра с id: " + genre.getId() + " не существует");
            }
        }
        return true;
    }
}
