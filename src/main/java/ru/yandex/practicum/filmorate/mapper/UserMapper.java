package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class UserMapper implements RowMapper<User> {
    Map<Long, User> users = new HashMap<>();

    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        User user = users.get(id);
        if (user == null) {
            user = User.builder()
                    .id(rs.getLong("id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .friendsId(new HashSet<>())
                    .build();
            users.put(id, user);
        }
        if (rs.getLong("USER2_ID") != 0) {
            if (!user.getFriendsId().contains(rs.getLong("USER2_ID"))) {
                user.getFriendsId().add(rs.getLong("USER2_ID"));
            }
        }
        if (rs.isLast()) {
            users.clear();
        }
        return user;
    }
}
