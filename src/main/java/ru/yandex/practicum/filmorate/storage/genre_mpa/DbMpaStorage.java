package ru.yandex.practicum.filmorate.storage.genre_mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DbMpaStorage implements MpaStorage {
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

    @Override
    public void mpaValidateNotFound(Long mpaId) {
        if (getById(mpaId) == null) {
            throw new NotFoundException("Рейтинг MPA с id: " + mpaId + "  не существует");
        }
    }

    @Override
    public void mpaValidateBadRequest(Long mpaId) {
        if (getById(mpaId) == null) {
            throw new ValidationException("Рейтинг MPA с id: " + mpaId + "  не существует");
        }
    }
}
