package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    @NotNull(groups = {Update.class, Delete.class})
    Long reviewId;
    @NotNull
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}