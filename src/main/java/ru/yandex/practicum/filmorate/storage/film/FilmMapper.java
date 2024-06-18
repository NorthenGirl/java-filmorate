package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
                    .idUserLike(new HashSet<>())
                    .mpa(MPA.builder()
                            .id(rs.getLong("rating_id"))
                            .name(rs.getString("rating_name"))
                            .build())
                    .build();
            films.put(id, film);
        }
        if (rs.getLong("id_user_like") != 0) {
            if (!film.getIdUserLike().contains(rs.getLong("id_user_like"))) {
                film.getIdUserLike().add(rs.getLong("id_user_like"));
            }
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
        if (rs.isLast()) {
            films.clear();
        }
        return film;
    }
}
