package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@AllArgsConstructor
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public Director create(Director director) {
        String sqlQuery = "insert into directors (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        if (getById(director.getId()) == null) {
            throw new NotFoundException("Режиссер с id " + director.getId() + " не найден");
        }
            String sqlQuery =
                    "UPDATE directors SET name = ? WHERE id = ?";
            jdbcTemplate.update(
                    sqlQuery,
                    director.getName(),
                    director.getId()
            );
            return director;
    }

    @Override
    public void delete(Long id) {
        try {
            String sqlQuery = "delete from directors WHERE id = ?";
            jdbcTemplate.update(sqlQuery, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Режиссера с id: " + id + " не существует");
        }
    }

    @Override
    public Director getById(Long id) {
        try {
            String sqlQery = "SELECT * FROM directors WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQery, directorMapper::mapRow, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Режиссера с id: " + id + " не существует");
        }
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM directors";
        List<Director> directors = jdbcTemplate.query(sqlQuery, directorMapper::mapRow);
        return directors;
    }

    @Override
    public void directorValidate(List<Director> directors) {
        for (Director director : directors) {
            if ((jdbcTemplate.query("SELECT * FROM directors WHERE id = ?", new DataClassRowMapper<>(Director.class), director.getId())).isEmpty()) {
                throw new ValidationException("Режиссера с id: " + director.getId() + " не существует");
            }
        }
    }
}
