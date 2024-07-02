package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DbLikesStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long userId, Long filmId) {
        String sqlQuery = "INSERT INTO likes VALUES (?, ?)";
        userLikeValidate(userId, filmId);
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
        log.info("внутри DbLikesStorage: удален ");
    }


    private void userLikeValidate(Long filmId, Long userId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ? AND user_id = ?";
        if ((jdbcTemplate.query(sqlQuery, new ColumnMapRowMapper(), filmId, userId)).contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " уже лайкнул этот фильм");
        }
    }
}
