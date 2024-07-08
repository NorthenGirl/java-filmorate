package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                content = ?, is_positive = ?, useful = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sqlQuery,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getUseful(),
                newReview.getReviewId()
        );

        return get(newReview.getReviewId()).get();
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
    public List<Review> getAll() {
        String sqlQuery = """
                SELECT id, content, is_positive, user_id, film_id, useful
                FROM reviews
                ORDER BY useful DESC
                """;

        return jdbcTemplate.query(sqlQuery, new ReviewMapper());
    }

    @Override
    public List<Review> getByFilmLimited(long filmId, Integer count) {
        String sqlQuery = """
                SELECT id, content, is_positive, user_id, film_id, useful
                FROM reviews
                WHERE film_id = ?
                ORDER BY useful DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sqlQuery, new ReviewMapper(), filmId, count);
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
        deleteReviewRating(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        deleteReviewRating(reviewId, userId, Boolean.FALSE);
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

    private void deleteReviewRating(long reviewId, long userId, boolean isPositive) {
        String sqlQuery = """
                DELETE FROM review_rating
                WHERE review_id = ? AND user_id = ? AND is_positive = ?
                """;

        jdbcTemplate.update(sqlQuery, reviewId, userId, isPositive);
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