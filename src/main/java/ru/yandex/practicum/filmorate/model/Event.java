package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Event {
    long eventId;
    long timestamp;
    long userId;
    EventType eventType;
    EventOperation operation;
    long entityId;
}
