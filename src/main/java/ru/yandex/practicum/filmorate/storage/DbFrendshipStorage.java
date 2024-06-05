package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbFrendshipStorage implements EnumStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Friendship getById(Long id) {
        String sqlQuery = "SELECT * FROM friendship_status WHERE status_id = ?";
        final List<Friendship> genres = jdbcTemplate.query(sqlQuery, DbFrendshipStorage::makeFriendship, id);
        if (genres.size() != 1) {
            throw new NotFoundException("genre id=" + id);
        }
        return genres.get(0);
    }

    @Override
    public List<Friendship> getAll() {
        String sqlQuery = "SELECT * FROM friendship_status";
        return jdbcTemplate.query(sqlQuery, DbFrendshipStorage::makeFriendship);
    }


    public Friendship getFromUser(Long user_id, Long friend_id) {
        String sqlQuery = "SELECT * FROM friendship_status WHERE id IN" +
                "(SELECT status_id FROM friends WHERE user_id AND friend_id IN" +
                "(SELECT user_id, friend_id FROM films WHERE user_id = ?, friend_id = ?))";
        return jdbcTemplate.queryForObject(sqlQuery, DbFrendshipStorage::makeFriendship, user_id, friend_id);
    }

    static Friendship makeFriendship(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
