package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {
    @Mock
    FilmStorage filmStorage;

    @InjectMocks
    FilmService filmService;

    @Test
    @DisplayName("test common film")
    void getCommonFilms() {
        Long userId = 12L;
        Long friendId = 18L;
        filmService.getCommonFilms(userId, friendId);
        Mockito.verify(filmStorage).getCommonFilms(userId, friendId);
    }
}