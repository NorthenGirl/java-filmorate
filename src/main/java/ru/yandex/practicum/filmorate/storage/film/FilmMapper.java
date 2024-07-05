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
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FilmMapper implements RowMapper<Film> {

    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("film_id");
        Film film = films.computeIfAbsent(id, k -> {
            try {
                return Film.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("releaseDate").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .genres(new ArrayList<>())
                        .directors(new ArrayList<>())
                        .mpa(MPA.builder()
                                .id(rs.getLong("rating_id"))
                                .name(rs.getString("rating_name"))
                                .build())
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        if (rs.getLong("genre_id") != 0) {
            Genre genre = Genre.builder()
                    .id(rs.getLong("genre_id"))
                    .name(rs.getString("genre_name"))
                    .build();
            if (!film.getGenres().contains(genre)) {
                film.getGenres().add(genre);
            }
        }

        if (rs.getLong("director_id") != 0) {
            Director director = Director.builder()
                    .id(rs.getLong("director_id"))
                    .name(rs.getString("director_name"))
                    .build();
            if (!film.getDirectors().contains(director)) {
                film.getDirectors().add(director);
            }
        }

        if (rs.isLast()) {
            LinkedHashMap<Long, Film> lastFilms = new LinkedHashMap<>(films);
            films.clear();
            return lastFilms.get(id);
        }
        return film;
    }
}
