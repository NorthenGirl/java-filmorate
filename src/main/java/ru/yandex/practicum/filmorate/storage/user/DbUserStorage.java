package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT " + "u.ID, " + "u.EMAIL, " + "u.LOGIN, " + "u.NAME, " + "u.BIRTHDAY, " + "f.USER2_ID " + "FROM USERS u " + "LEFT JOIN FRIENDS f ON (f.USER1_ID  = u.ID)";
        return jdbcTemplate.query(sqlQuery, userMapper::mapRow);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        updateValidate(user);
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public long delete(Long id) {
        String deleteFriendsQuery = "DELETE FROM friends WHERE user1_id = ? OR user2_id = ?";
        jdbcTemplate.update(deleteFriendsQuery, id, id);

        String sqlQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);

        return 0;
    }

    @Override
    public User getUser(Long id) {
        final String sqlQuery = "SELECT u.ID,\n" + "u.EMAIL,\n" + "u.LOGIN,\n" + "u.NAME,\n" + "u.BIRTHDAY,\n" + "f.USER2_ID\n" + "FROM  USERS AS u\n" + "LEFT JOIN FRIENDS AS f ON u.ID = f.USER1_ID\n" + "WHERE u.ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, userMapper::mapRow, id);
        if (users.size() == 0) {
            throw new NotFoundException("Пользователь с  id = " + id + " не найден");
        }
        return users.get(0);
    }

    private void updateValidate(User user) {
        if (user.getId() == null) {
            throw new NotFoundException("Id пользователя должен быть указан");
        }
        final String sqlQuery = "SELECT u.ID,\n" + "u.EMAIL,\n" + "u.LOGIN,\n" + "u.NAME,\n" + "u.BIRTHDAY,\n" + "f.USER2_ID\n" + "FROM  USERS AS u\n" + "LEFT JOIN FRIENDS AS f ON u.ID = f.USER1_ID\n" + "WHERE u.ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, userMapper::mapRow, user.getId());
        if (users.size() != 1) {
            throw new NotFoundException("Пользователь с  id = " + user.getId() + " не найден");
        }
    }
}
