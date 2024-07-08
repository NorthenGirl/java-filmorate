package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import org.hibernate.sql.Update;

public abstract class StorageData {
    @NotNull(groups = {Update.class})
    private Long id;
}
