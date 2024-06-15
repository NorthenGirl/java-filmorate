package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
@RequiredArgsConstructor
@Component
public class DbFriendsStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long user_id, Long friend_id) {
        String sqlQuery = "INSERT INTO friends (user1_id, user2_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user_id, friend_id, 1);
    }

    @Override
    public void deleteFriend(Long user_id, Long friend_id) {
        String sqlQuery = "delete from friends WHERE user1_id = ? AND user2_id = ?";
        jdbcTemplate.update(sqlQuery, user_id, friend_id);

    }

    @Override
    public List<User> getFriends(Long user_id) {
        String sqlQuery = "SELECT * FROM users WHERE id IN (SELECT user2_id FROM friends WHERE user1_id = ? AND status = 1)";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(User.class), user_id);
    }

    @Override
    public List<User> getIdCommonFriends(Long userId, Long commonId) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE id IN (\n" +
                "    SELECT DISTINCT user2_id\n" +
                "    FROM friends\n" +
                "    WHERE user1_id = ? AND status = 1\n" +
                "    AND user2_id IN (\n" +
                "        SELECT DISTINCT user2_id\n" +
                "        FROM friends\n" +
                "        WHERE user1_id = ? AND status = 1\n" +
                "    )\n" +
                ")";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(User.class), userId, commonId);
    }
}
