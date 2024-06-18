package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.sql.Update;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class StorageData {
    @NotNull(groups = {Update.class})
    Long id;
}
