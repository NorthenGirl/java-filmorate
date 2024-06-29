package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    void delete(Long id);

    Director getById(Long id);

    List<Director> getAll();

    void directorValidate(List<Director> directors);
}
