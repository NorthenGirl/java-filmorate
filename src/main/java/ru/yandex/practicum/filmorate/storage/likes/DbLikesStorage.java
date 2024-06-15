package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RequiredArgsConstructor
@Component
public class DbLikesStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long user_id, Long film_id) {
        String sqlQuery = "INSERT INTO likes VALUES (?, ?)";
        userLikeValidate(user_id, film_id);
        jdbcTemplate.update(sqlQuery, user_id, film_id);
    }

    @Override
    public void deleteLike(Long user_id, Long film_id) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, user_id, film_id);
    }


    private void userLikeValidate(Long filmId, Long userId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ? AND user_id = ?";
        if ((jdbcTemplate.query(sqlQuery, new ColumnMapRowMapper(), filmId, userId)).contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " уже лайкнул этот фильм");
        }
    }
}
