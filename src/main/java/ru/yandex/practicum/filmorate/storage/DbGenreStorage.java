package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbGenreStorage implements EnumStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getById(Long id) {
        String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, DbGenreStorage::makeGenre, id);
        if (genres.size() != 1) {
            throw new NotFoundException("genre id=" + id);
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery, DbGenreStorage::makeGenre);
    }

    public Genre getFromFilm(Long film_id) {
        String sqlQuery = "SELECT * FROM genres WHERE id IN (SELECT genre_id FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, DbGenreStorage::makeGenre, film_id);
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
