package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
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
        String sqlQery = """
                SELECT
                    FLM.FILM_ID,
                    FLM.NAME,
                    FLM.DESCRIPTION,
                    FLM.RELEASEDATE,
                    FLM.DURATION,
                    MR.ID AS RATING_ID,
                    MR.NAME AS RATING_NAME,
                    G2.ID AS GENRE_ID,
                    G2.NAME AS GENRE_NAME,
                    D.ID AS DIRECTOR_ID,
                    D.NAME AS DIRECTOR_NAME
                FROM
                    FILMS AS FLM
                LEFT JOIN MPA_RATING MR ON MR.ID = FLM.RATING_ID
                LEFT JOIN FILM_GENRES FG ON FLM.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G2 ON G2.ID = FG.GENRE_ID
                LEFT JOIN FILM_DIRECTORS FD ON FLM.FILM_ID = FD.FILM_ID
                LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID

                """;
        List<Film> films = jdbcTemplate.query(sqlQery, filmMapper::mapRow);

        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery =
                "INSERT INTO films (name, description, releaseDate, duration, rating_id) values (?, ?, ?, ?, ?)";

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
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                if (genre.getId() != null) {
                    jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id)values(?,?)", +
                            film.getId(), genre.getId());
                }
            }
        }
        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            for (Director director : directors) {
                if (director.getId() != null) {
                    jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) values (?, ?)", +
                            film.getId(), director.getId());
                }
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        updateValidate(film);
        String sqlQuery = "update films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ?" +
                " WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                if (genre.getId() != null) {
                    jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) values(?,?)", +
                            film.getId(), genre.getId());
                }
            }
        }
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            for (Director director : directors) {
                if (director.getId() != null) {
                    jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) values (?, ?)", film.getId(),
                            director.getId());
                }
            }

        }
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "delete from films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public Film getFilm(Long id) {
        final String sqlQuery = """
                SELECT
                    FLM.FILM_ID,
                    FLM.NAME,
                    FLM.DESCRIPTION,
                    FLM.RELEASEDATE,
                    FLM.DURATION,
                    MR.ID AS RATING_ID,
                    MR.NAME AS RATING_NAME,
                    G2.ID AS GENRE_ID,
                    G2.NAME AS GENRE_NAME,
                    D.ID AS DIRECTOR_ID,
                    D.NAME AS DIRECTOR_NAME
                FROM FILMS AS FLM
                LEFT JOIN MPA_RATING MR ON MR.ID = FLM.RATING_ID
                LEFT JOIN FILM_GENRES FG ON FLM.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G2 ON G2.ID = FG.GENRE_ID
                LEFT JOIN FILM_DIRECTORS FD ON FLM.FILM_ID = FD.FILM_ID
                LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID
                WHERE FLM.FILM_ID = ?
                """;
        List<Film> films = jdbcTemplate.query(sqlQuery, filmMapper::mapRow, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с  id=" + id + "не найден");
        }
        return films.get(0);
    }

    @Override
    public List<Film> getIdPopularFilms(Integer count, Long genreId, Integer year) {
        if (count == null) {
            count = 10;
        }
        List<Object> params = new ArrayList<>();
        StringBuilder sqlQuery = new StringBuilder("""
                SELECT
                    FLM.FILM_ID,
                    FLM.NAME,
                    FLM.DESCRIPTION,
                    FLM.RELEASEDATE,
                    FLM.DURATION,
                    MR.ID AS RATING_ID,
                    MR.NAME AS RATING_NAME,
                    G2.ID AS GENRE_ID,
                    G2.NAME AS GENRE_NAME,
                    D.ID AS DIRECTOR_ID,
                    D.NAME AS DIRECTOR_NAME
                FROM FILMS AS FLM
                LEFT JOIN MPA_RATING MR ON MR.ID = FLM.RATING_ID
                LEFT JOIN FILM_GENRES FG ON FLM.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G2 ON G2.ID = FG.GENRE_ID
                LEFT JOIN FILM_DIRECTORS FD ON FLM.FILM_ID = FD.FILM_ID
                LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID
                LEFT JOIN (SELECT film_id, COUNT(user_id) as likes_count FROM LIKES GROUP BY film_id) l
                ON (FLM.FILM_ID = l.FILM_ID) WHERE TRUE\s""");
        if (genreId != null) {
            sqlQuery.append("""
                    AND GENRE_ID = ?\s""");
            params.add(genreId);
        }
        if (year != null) {
            sqlQuery.append("""
                    AND EXTRACT(YEAR FROM FLM.RELEASEDATE) = ?\s""");
            params.add(year);
        }
        sqlQuery.append("""        
                ORDER BY l.likes_count DESC
                LIMIT ?
                """);
        params.add(count);
        return jdbcTemplate.query(sqlQuery.toString(), filmMapper::mapRow, params.toArray());
    }

    @Override
    public List<Film> getFilmsByDirectorIdSortedByLikes(Long directorId) {
        String sqlQuery = """
                SELECT
                    FLM.FILM_ID,
                    FLM.NAME,
                    FLM.DESCRIPTION,
                    FLM.RELEASEDATE,
                    FLM.DURATION,
                    MR.ID AS RATING_ID,
                    MR.NAME AS RATING_NAME,
                    G2.ID AS GENRE_ID,
                    G2.NAME AS GENRE_NAME,
                    D.ID AS DIRECTOR_ID,
                    D.NAME AS DIRECTOR_NAME
                FROM FILM_DIRECTORS fd
                LEFT JOIN FILMS FLM ON FLM.FILM_ID = fd.FILM_ID
                LEFT JOIN MPA_RATING MR ON MR.ID = FLM.RATING_ID
                LEFT JOIN FILM_GENRES FG ON FLM.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G2 ON G2.ID = FG.GENRE_ID
                LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID
                LEFT JOIN (SELECT film_id, COUNT(user_id) likes_count FROM LIKES GROUP BY film_id) L
                ON (FLM.FILM_ID = L.FILM_ID)
                WHERE fd.director_id = ?
                GROUP BY FLM.film_id
                ORDER BY L.likes_count DESC
                """;
        return jdbcTemplate.query(sqlQuery, filmMapper::mapRow, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorIdSortedByYear(Long directorId) {
        String sqlQuery = """
                SELECT
                    FLM.FILM_ID,
                    FLM.NAME,
                    FLM.DESCRIPTION,
                    FLM.RELEASEDATE,
                    FLM.DURATION,
                    MR.ID AS RATING_ID,
                    MR.NAME AS RATING_NAME,
                    G2.ID AS GENRE_ID,
                    G2.NAME AS GENRE_NAME,
                    D.ID AS DIRECTOR_ID,
                    D.NAME AS DIRECTOR_NAME
                FROM FILM_DIRECTORS fd
                LEFT JOIN FILMS FLM ON FLM.FILM_ID = fd.FILM_ID
                LEFT JOIN MPA_RATING MR ON MR.ID = FLM.RATING_ID
                LEFT JOIN FILM_GENRES FG ON FLM.FILM_ID = FG.FILM_ID
                LEFT JOIN GENRES G2 ON G2.ID = FG.GENRE_ID
                LEFT JOIN DIRECTORS D ON D.ID = FD.DIRECTOR_ID
                WHERE fd.director_id = ?
                GROUP BY FLM.film_id
                ORDER BY EXTRACT(YEAR FROM FLM.releaseDate)
                """;
        List<Film> films = jdbcTemplate.query(sqlQuery, filmMapper::mapRow, directorId);
        return films;
    }


    private void updateValidate(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (getFilm(film.getId()) == null) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
    }
}

