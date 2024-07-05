package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    long eventId;
    long timestamp;
    long userId;
    EventType eventType;
    EventOperation operation;
    long entityId;
}