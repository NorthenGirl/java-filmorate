package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class MPA {
    @NotNull
    private final Long id;
    private String name;

    public MPA(Long id) {
        this.id = id;
    }

    public MPA(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
