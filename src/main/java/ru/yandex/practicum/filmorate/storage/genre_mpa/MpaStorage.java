package ru.yandex.practicum.filmorate.storage.genre_mpa;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MpaStorage {
    MPA getById(Long id);

    List<MPA> getAll();

    void mpaValidateNotFound(Long mpaId);

    void mpaValidateBadRequest(Long mpaId);
}
