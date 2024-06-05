package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Friendship {
    @NotNull
    private Long status_id;
    @NotBlank
    private String name;

    public Friendship(Long status_id, String name) {
        this.status_id = status_id;
        this.name = name;
    }
}
