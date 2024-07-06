package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review newReview);

    void delete(Long id);

    Optional<Review> get(Long id);

    List<Review> getAll();

    List<Review> getByFilm(Long id);

    void addLikeToReview(Long reviewId, Long userId);

    void addDislikeToReview(Long reviewId, Long userId);

    void deleteLikeToReview(Long reviewId, Long userId);

    void deleteDislikeToReview(Long reviewId, Long userId);
}