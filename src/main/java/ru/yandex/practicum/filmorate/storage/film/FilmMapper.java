package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilmMapper implements RowMapper<Film> {
    private Map<Long, Film> films = new HashMap<>();

    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("FILM_ID");
        Film film = films.get(id);
        if (film == null) {
            film = Film.builder()
                    .id(id)
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASEDATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .genres(new ArrayList<Genre>())
                    .directors(new ArrayList<Director>())
                    .mpa(MPA.builder()
                            .id(rs.getLong("RATING_ID"))
                            .name(rs.getString("RATING_NAME"))
                            .build())
                    .build();
            films.put(id, film);
        }

        if (rs.getLong("GENRE_ID") != 0) {
            if (!film.getGenres().contains(Genre.builder()
                    .id(rs.getLong("GENRE_ID"))
                    .name(rs.getString("GENRE_NAME"))
                    .build())) {
                film.getGenres().add(Genre.builder()
                        .id(rs.getLong("GENRE_ID"))
                        .name(rs.getString("GENRE_NAME"))
                        .build());
            }
        }

        if (rs.getLong("DIRECTOR_ID") != 0) {
            if (!film.getDirectors().contains(Director.builder()
                    .id(rs.getLong("DIRECTOR_ID"))
                    .name(rs.getString("DIRECTOR_NAME"))
                    .build()) || film.getDirectors().isEmpty()) {
                film.getDirectors().add(Director.builder()
                        .id(rs.getLong("DIRECTOR_ID"))
                        .name(rs.getString("DIRECTOR_NAME"))
                        .build());
            }
        }
        if (rs.isLast()) {
            films.clear();
        }
        return film;
    }
}
