package ru.yandex.practicum.filmorate.storage.genre_mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DataClassRowMapper<Genre> dataClassRowMapper=new DataClassRowMapper<>(Genre.class);

    @Override
    public Genre getById(Long id) {
        try {
            String sqlQuery = "SELECT * FROM genres WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, dataClassRowMapper, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Жанра с id: " + id + " не существует");
        }
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, dataClassRowMapper);
    }

    @Override
    public void genreValidate(List<Genre> genres) {
        for (Genre genre : genres) {
            if ((jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", dataClassRowMapper, genre.getId())).isEmpty()) {
                throw new ValidationException("Жанра с id: " + genre.getId() + " не существует");
            }
        }
    }

}
