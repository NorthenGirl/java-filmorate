package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbMpaRatingStorage implements EnumStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getById(Long id) {
        String sqlQuery = "SELECT * FROM mpa_rating WHERE id = ?";
        final List<MPA> ratings = jdbcTemplate.query(sqlQuery, DbMpaRatingStorage::makeMPA, id);
        if (ratings.size() != 1) {
            throw new NotFoundException("raiting id=" + id);
        }
        return ratings.get(0);
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, DbMpaRatingStorage::makeMPA);
    }

    public MPA getFromFilm(Long film_id) {
        String sqlQuery = "SELECT * FROM mpa_rating WHERE id IN (SELECT rating_id FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, DbMpaRatingStorage::makeMPA, film_id);
    }

    static MPA makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
