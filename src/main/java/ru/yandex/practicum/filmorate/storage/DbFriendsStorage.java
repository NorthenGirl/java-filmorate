package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;
@RequiredArgsConstructor
@Component
public class DbFriendsStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long friend_id, Long user_id) {
        String sqlQuery = "INSERT INTO friends VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, friend_id, user_id);
    }

    @Override
    public void deleteFriend(Long friend_id, Long user_id) {
        String sqlQuery = "delete from friends WHERE friend_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, friend_id, user_id);

    }

    @Override
    public List<Long> getIdFriends(Long user_id) {
        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, user_id);
    }

    @Override
    public List<Long> getIdCommonFriends(Long userId, Long commonId) {
        String sqlQuery = "select DISTINCT friend_id from friends WHERE user_id = ? AND" +
                "friend_id IN (select friend_id from friends WHERE user_id = ?)";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId, commonId);
    }
}
