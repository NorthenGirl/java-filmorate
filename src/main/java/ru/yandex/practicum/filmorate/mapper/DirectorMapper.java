package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DirectorMapper implements RowMapper<Director> {
    private Map<Long, Director> directors = new HashMap<>();

    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        Director director = directors.get(id);
        if (director == null) {
            director = Director.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .build();
            director.setId(id);
        }
        if (rs.isLast()) {
            directors.clear();
        }
        return director;
    }
}
