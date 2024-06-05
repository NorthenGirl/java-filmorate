package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.annotations.IsAfterDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @IsAfterDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> idUserLike;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.idUserLike = new HashSet<>();
    }
}


