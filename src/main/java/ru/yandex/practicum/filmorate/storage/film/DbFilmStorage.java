package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public List<Film> findAll() {
        String sqlQery = "SELECT\n" +
                "    FLM.FILM_ID,\n" +
                "    FLM.NAME,\n" +
                "    FLM.DESCRIPTION,\n" +
                "    FLM.RELEASEDATE,\n" +
                "    FLM.DURATION,\n" +
                "    L.USER_ID AS ID_USER_LIKE,\n" +
                "    MR.ID AS RATING_ID,\n" +
                "    MR.NAME AS RATING_NAME,\n" +
                "    G2.ID AS GENRE_ID,\n" +
                "    G2.NAME AS GENRE_NAME\n" +
                "from FILMS AS FLM\n" +
                "left join LIKES L on FLM.FILM_ID = L.FILM_ID\n" +
                "left join MPA_RATING MR on MR.ID = FLM.RATING_ID\n" +
                "left join FILM_GENRES FG on FLM.FILM_ID = FG.FILM_ID\n" +
                "left join GENRES G2 on G2.ID = FG.GENRE_ID";
        return jdbcTemplate.query(sqlQery, filmMapper::mapRow);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, releaseDate, duration, rating_id) values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                if (genre.getId() != null) {
                    jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id)values(?,?)", film.getId(), genre.getId());
                }
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        updateValidate(film);
        String sqlQuery =
                "update films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "delete from films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public Film getFilm(Long id) {
        final String sqlQuery = "SELECT\n" +
                "    FLM.FILM_ID,\n" +
                "    FLM.NAME,\n" +
                "    FLM.DESCRIPTION,\n" +
                "    FLM.RELEASEDATE,\n" +
                "    FLM.DURATION,\n" +
                "    L.USER_ID AS ID_USER_LIKE,\n" +
                "    MR.ID AS RATING_ID,\n" +
                "    MR.NAME AS RATING_NAME,\n" +
                "    G2.ID AS GENRE_ID,\n" +
                "    G2.NAME AS GENRE_NAME\n" +
                "from FILMS AS FLM\n" +
                "left join LIKES L on FLM.FILM_ID = L.FILM_ID\n" +
                "left join MPA_RATING MR on MR.ID = FLM.RATING_ID\n" +
                "left join FILM_GENRES FG on FLM.FILM_ID = FG.FILM_ID\n" +
                "left join GENRES G2 on G2.ID = FG.GENRE_ID\n" +
                "WHERE FLM.FILM_ID = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, filmMapper::mapRow, id);
        if (films.size() == 0) {
            throw new NotFoundException("Фильм с  id=" + id + "не найден");
        }
        return films.get(0);
    }

    @Override
    public List<Film> getIdPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        String sqlQuery = "SELECT f.film_id id, f.name, l.likes_count\n" +
                "FROM FILMS f\n" +
                " LEFT JOIN (SELECT film_id, COUNT(user_id) likes_count  FROM LIKES  GROUP BY  film_id) l " +
                "ON (f.FILM_ID = L.FILM_ID)\n" +
                "ORDER BY l.likes_count DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(Film.class), count);
    }

    public void updateValidate(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (getFilm(film.getId()) == null) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
    }
}

