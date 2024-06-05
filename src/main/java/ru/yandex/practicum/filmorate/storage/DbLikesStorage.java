package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
@RequiredArgsConstructor
@Component
public class DbLikesStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long film_id, Long user_id) {
        String sqlQuery = "INSERT INTO likes VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film_id, user_id);
    }

    @Override
    public void deleteLike(Long film_id, Long user_id) {
        String sqlQuery = "DELETE INTO likes VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film_id, user_id);
    }

    @Override
    public List<Long> getIdPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        String sqlQuery = "SELECT film_id,\n" +
                "FROM likes\n" +
                "GROUP BY film_id\n" +
                "ORDER BY COUNT(user_id)\n" +
                "LIMIT = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, count);
    }
}
