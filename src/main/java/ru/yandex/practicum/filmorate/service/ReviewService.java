package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review review) {
        reviewValidation(review);
        return reviewStorage.create(review);
    }

    public Review update(Review newReview) {
        reviewValidation(newReview);
        return reviewStorage.update(newReview);
    }

    public void delete(long id) {
        checkReviewId(id);
        reviewStorage.delete(id);
    }

    public Review get(long id) {
        return reviewStorage.get(id).orElseThrow(
                () -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    public List<Review> getByFilm(long filmId, int count) {
        List<Review> reviews;

        if (filmId == -1) {
            reviews = reviewStorage.getALl();
        } else {
            reviews = reviewStorage.getByFilm(filmId);
        }
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
        }
        return reviews;
    }

    public void addLikeToReview(long reviewId, long userId) {
        setUserReaction(reviewId, userId, true);
    }

    public void addDislikeToReview(long reviewId, long userId) {
        setUserReaction(reviewId, userId, false);
    }

    public void deleteLikeToReview(long reviewId, long userId) {
        clearUserReaction(reviewId, userId, true);
    }

    public void deleteDislikeToReview(long reviewId, long userId) {
        clearUserReaction(reviewId, userId, false);
    }

    private void setUserReaction(long reviewId, long userId, boolean isLike) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (isLike) {
            reviewStorage.addLikeToReview(reviewId, userId);
        } else {
            reviewStorage.addDislikeToReview(reviewId, userId);
        }
    }

    private void clearUserReaction(long reviewId, long userId, boolean isLike) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (isLike) {
            reviewStorage.deleteLikeToReview(reviewId, userId);
        } else {
            reviewStorage.deleteDislikeToReview(reviewId, userId);
        }
    }

    private void reviewValidation(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Содержание отзыва пустое");
        }
        if (review.getUserId() == null || userService.getUser(review.getUserId()) == null) {
            throw new ValidationException("Данные о пользователе отсутствуют");
        }
        if (review.getFilmId() == null || filmService.getFilm(review.getFilmId()) == null) {
            throw new ValidationException("Данные о фильме отсутствуют");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва не определен");
        }
        review.setUseful(0L);
    }

    private void checkReviewId(long reviewId) {
        if (reviewStorage.get(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
    }

    private void checkUserId(long userId) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}
