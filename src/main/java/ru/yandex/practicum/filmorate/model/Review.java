package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Review {
    @NotNull(groups = {Update.class})
    Long reviewId;
    @NotNull
    @NotBlank
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}