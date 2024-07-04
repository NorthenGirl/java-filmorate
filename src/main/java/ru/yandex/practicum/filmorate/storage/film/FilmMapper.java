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
        Long id = rs.getLong("film_id");
        Film film = films.get(id);
        if (film == null) {
            film = Film.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("releaseDate").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .genres(new ArrayList<Genre>())
                    .directors(new ArrayList<Director>())
                    .mpa(MPA.builder()
                            .id(rs.getLong("rating_id"))
                            .name(rs.getString("rating_name"))
                            .build())
                    .build();
            films.put(id, film);
        }

        if (rs.getLong("genre_id") != 0) {
            if (!film.getGenres().contains(Genre.builder()
                    .id(rs.getLong("genre_id"))
                    .name(rs.getString("genre_name"))
                    .build())) {
                film.getGenres().add(Genre.builder()
                        .id(rs.getLong("genre_id"))
                        .name(rs.getString("genre_name"))
                        .build());
            }
        }

        if (rs.getLong("director_id") != 0) {
            if (!film.getDirectors().contains(Director.builder()
                    .id(rs.getLong("director_id"))
                    .name(rs.getString("director_name"))
                    .build()) || film.getDirectors().size() == 0) {
                film.getDirectors().add(Director.builder()
                        .id(rs.getLong("director_id"))
                        .name(rs.getString("director_name"))
                        .build());
            }
        }
        if (rs.isLast()) {
            films.clear();
        }
        return film;
    }
}
