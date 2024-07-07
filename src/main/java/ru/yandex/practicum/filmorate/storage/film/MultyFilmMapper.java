package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MultyFilmMapper implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> films = new LinkedHashMap<>();
        while (rs.next()) {
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
                Genre genre = Genre.builder()
                        .id(rs.getLong("GENRE_ID"))
                        .name(rs.getString("GENRE_NAME"))
                        .build();
                if (film.getGenres().isEmpty() || !film.getGenres().contains(genre)) {
                    film.getGenres().add(genre);
                }
            }

            if (rs.getLong("DIRECTOR_ID") != 0) {
                Director director = Director.builder()
                        .id(rs.getLong("DIRECTOR_ID"))
                        .name(rs.getString("DIRECTOR_NAME"))
                        .build();
                if (film.getDirectors().isEmpty() || !film.getDirectors().contains(director)) {
                    film.getDirectors().add(director);
                }
            }
        }
        return films.values().stream().toList();
    }
}
