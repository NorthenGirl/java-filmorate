package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbFrendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DataClassRowMapper<Friendship> dataClassRowMapper = new DataClassRowMapper<>(Friendship.class);

    @Override
    public Friendship getById(Long id) {
        String sqlQuery = "SELECT * FROM friendship_status WHERE status_id = ?";
        final List<Friendship> friendshipStatus = jdbcTemplate.query(sqlQuery, dataClassRowMapper, id);
        if (CollectionUtils.isEmpty(friendshipStatus)) {
            throw new NotFoundException("Статуса дружбы с id = " + id + " не существует");
        }
        return friendshipStatus.get(0);
    }

    @Override
    public List<Friendship> getAll() {
        String sqlQuery = "SELECT * FROM friendship_status";
        return jdbcTemplate.query(sqlQuery, dataClassRowMapper);
    }
}
