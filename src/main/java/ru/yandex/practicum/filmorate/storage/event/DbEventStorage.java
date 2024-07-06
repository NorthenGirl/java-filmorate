package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbEventStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(long userId) {
        String sqlQuery = """
                SELECT event_id, user_id, timestamp, event_type, operation, entity_id
                FROM feed
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(sqlQuery, new EventMapper(), userId);
    }

    @Override
    public void createEvent(Event event) {
        String sqlQuery = """
               INSERT INTO feed (
               user_id, timestamp, event_type, operation, entity_id
               ) VALUES (?, ?, ?, ?, ?)
               """;

        jdbcTemplate.update(
                sqlQuery,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );
    }
}