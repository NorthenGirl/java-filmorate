package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Event {
    long eventId;
    long timestamp;
    long userId;
    EventType eventType;
    EventOperation operation;
    long entityId;
}