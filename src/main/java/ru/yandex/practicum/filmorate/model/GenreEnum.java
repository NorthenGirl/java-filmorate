package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum GenreEnum {
    COMEDY(1L, "Комедия"),
    DRAMA(2L, "Драма"),
    CARTOON(3L, "Мультфильм"),
    THRILLER(4L, "Триллер"),
    DOCUMENTARY(5L, "Документальный"),
    ACTION(6L, "Боевик");

    private final Long genreDBId;
    private final String genreDBName;
    private static final Map<Long, String> data = Arrays.stream(GenreEnum.values())
            .collect(Collectors.toMap(GenreEnum::getGenreDBId, GenreEnum::getGenreDBName));


    public static String getNameByDBId(Long id) {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            throw new ValidationException("Incorrect genre number");
        }
    }

}
