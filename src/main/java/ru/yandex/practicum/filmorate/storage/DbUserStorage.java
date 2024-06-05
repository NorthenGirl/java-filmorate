package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbUserStorage implements LikesStorage.UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final OrderedFormContentFilter formContentFilter;

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select * from users", DbUserStorage::makeUser);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
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
        String sqlQuery =
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    private void updateValidate(User user) {
        if (user.getId() == null) {
            throw new NotFoundException("Id пользователя должен быть указан");
        }
        final String sqlQuery = "select * from users WHERE user_id = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, DbUserStorage::makeUser, user.getId());
        if (users.size() != 1) {
            throw new NotFoundException("user id=" + user.getId());
        }
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "delete from users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User getUser(Long id) {
        final String sqlQuery = "select * from users WHERE user_id = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, DbUserStorage::makeUser, id);
        if (users.size() != 1) {
            throw new NotFoundException("user id=" + id);
        }
        return users.get(0);
    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }


}
