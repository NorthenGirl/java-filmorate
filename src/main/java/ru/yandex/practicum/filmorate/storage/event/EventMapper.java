package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}