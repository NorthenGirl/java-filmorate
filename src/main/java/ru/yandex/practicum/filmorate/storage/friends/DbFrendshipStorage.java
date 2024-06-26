package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DbFrendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Friendship getById(Long id) {
        String sqlQuery = "SELECT * FROM friendship_status WHERE status_id = ?";
        final List<Friendship> friendshipStatus = jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(Friendship.class), id);
        if (CollectionUtils.isEmpty(friendshipStatus)) {
            throw new NotFoundException("Статуса дружбы с id = " + id + " не существует");
        }
        return friendshipStatus.get(0);
    }

    @Override
    public List<Friendship> getAll() {
        String sqlQuery = "SELECT * FROM friendship_status";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(Friendship.class));
    }
}
