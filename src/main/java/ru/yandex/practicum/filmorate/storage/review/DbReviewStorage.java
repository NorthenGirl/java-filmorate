package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DbReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sqlQuery = """
                INSERT INTO reviews (
                content, is_positive, user_id, film_id, useful
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review newReview) {
        String sqlQuery = """
                UPDATE reviews SET
                content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sqlQuery,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getUserId(),
                newReview.getFilmId(),
                newReview.getUseful(),
                newReview.getReviewId()
        );

        return newReview;
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Optional<Review> get(long id) {
        String sqlQuery = """
                SELECT id, content, is_positive, user_id, film_id, useful
                FROM reviews
                WHERE id = ?
                """;

        return jdbcTemplate.query(sqlQuery, new ReviewMapper(), id).stream().findFirst();
    }

    @Override
    public List<Review> getALl() {
        String sqlQuery = """
                SELECT id, content, is_positive, user_id, film_id, useful
                FROM reviews
                ORDER BY useful DESC
                """;

        List<Review> reviews = jdbcTemplate.query(sqlQuery, new ReviewMapper());
        Set<Review> uniqueReview = new TreeSet<>(Comparator.comparing(Review::getReviewId));
        uniqueReview.addAll(reviews);
        return new ArrayList<>(uniqueReview);
    }

    @Override
    public List<Review> getByFilm(long id) {
        String sqlQuery = """
                SELECT id, content, is_positive, user_id, film_id, useful
                FROM reviews
                WHERE film_id = ?
                ORDER BY useful DESC
                """;

        List<Review> reviews = jdbcTemplate.query(sqlQuery, new ReviewMapper(), id);
        Set<Review> uniqueReview = new TreeSet<>(Comparator.comparing(Review::getReviewId));
        uniqueReview.addAll(reviews);
        return new ArrayList<>(uniqueReview);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        addReviewRating(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        addReviewRating(reviewId, userId, Boolean.FALSE);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        deleteReviewRating(reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        deleteDislikeToReview(reviewId, userId);
    }

    private void addReviewRating(long reviewId, long userId, boolean isPositive) {
        String sqlQuery = """
                MERGE INTO review_rating (
                review_id, user_id, is_positive
                )
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sqlQuery, reviewId, userId, isPositive);
        updateReviewRating(reviewId);
    }

    private void deleteReviewRating(long reviewId, long userId) {
        String sqlQuery = """
                DELETE FROM review_rating
                WHERE review_id = ? AND user_id = ?
                """;

        jdbcTemplate.update(sqlQuery, reviewId, userId);
        updateReviewRating(reviewId);
    }

    private void updateReviewRating(long reviewId) {
        long usefulRating = getReviewUseful(reviewId);

        String sqlQuery = """
                UPDATE reviews SET useful = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sqlQuery, usefulRating, reviewId);
    }

    private long getReviewUseful(long reviewId) {
        String sqlQuery = """
                SELECT SUM(
                CASE WHEN is_positive = TRUE THEN 1 ELSE -1 END) useful
                FROM review_rating
                WHERE review_id = ?
                """;

        return jdbcTemplate.query(sqlQuery, new ReviewRatingMapper(), reviewId).stream().findAny().orElseThrow(null);
    }
}