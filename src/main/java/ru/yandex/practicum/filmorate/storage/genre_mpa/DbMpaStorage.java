package ru.yandex.practicum.filmorate.storage.genre_mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DbMpaStorage implements EnumStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getById(Long id) {
        try {
            String sqlQuery = "SELECT * FROM mpa_rating WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new DataClassRowMapper<>(MPA.class), id);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(MPA.class));
    }

    public MPA getFromFilm(Long filmId) {
        String sqlQuery = "SELECT * FROM mpa_rating WHERE id IN (SELECT rating_id FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, new DataClassRowMapper<>(MPA.class), filmId);
    }
}
