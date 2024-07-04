package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review newReview);

    void delete(long id);

    Optional<Review> get(long id);

    List<Review> getALl();

    List<Review> getByFilm(long id);

    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void deleteLikeToReview(long reviewId, long userId);

    void deleteDislikeToReview(long reviewId, long userId);
}