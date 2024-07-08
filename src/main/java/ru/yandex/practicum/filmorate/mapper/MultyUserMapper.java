package ru.yandex.practicum.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class MultyUserMapper implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> users = new LinkedHashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("id");
            User user = users.get(userId);
            if (isNull(user)) {
                user = User.builder()
                        .id(rs.getLong("id"))
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .name(rs.getString("name"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .friendsId(new HashSet<>())
                        .build();
                users.put(userId, user);
            }
            long friendId = rs.getLong("USER2_ID");
            if (friendId != 0) {
                user.getFriendsId().add(friendId);
            }
        }
        return users.values().stream().toList();
    }
}
