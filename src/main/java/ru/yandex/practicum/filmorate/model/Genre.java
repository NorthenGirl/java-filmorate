package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Genre {
    @NotNull
    private final Long genre_id;
    @NotBlank
    private final String name;
   public Genre(long genreId, String name) {
        this.genre_id = genreId;
        this.name = name;
    }
}
