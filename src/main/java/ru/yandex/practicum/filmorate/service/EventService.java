package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public List<Event> getFeed(long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с  id = " + userId + " не найден");
        }
        return eventStorage.getFeed(userId);
    }

    public void createEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(eventType)
                .operation(eventOperation)
                .entityId(entityId)
                .build();

        eventStorage.createEvent(event);
    }
}
