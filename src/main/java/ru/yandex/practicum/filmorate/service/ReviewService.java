package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
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
    private final EventService eventService;

    public Review create(Review review) {
        reviewValidation(review);
        Review createdReview = reviewStorage.create(review);
        eventService.createEvent(createdReview.getUserId(), EventType.REVIEW, EventOperation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public Review update(Review newReview) {
        reviewValidation(newReview);
        Review updatedReview = reviewStorage.update(newReview);
        eventService.createEvent(updatedReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }

    public void delete(Long id) {
        Review review = reviewStorage.get(id).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + id + " не найден"));
        reviewStorage.delete(id);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
    }

    public Review get(Long id) {
        return reviewStorage.get(id).orElseThrow(
                () -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    public List<Review> getByFilm(Long filmId, int count) {
        List<Review> reviews;

        if (filmId == null) {
            reviews = reviewStorage.getAll();
        } else {
            reviews = reviewStorage.getByFilm(filmId);
        }
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
        }
        return reviews;
    }

    public void addLikeToReview(Long reviewId, Long userId) {
        setUserReaction(reviewId, userId, true);
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        setUserReaction(reviewId, userId, false);
    }

    public void deleteLikeToReview(Long reviewId, Long userId) {
        clearUserReaction(reviewId, userId, true);
    }

    public void deleteDislikeToReview(Long reviewId, Long userId) {
        clearUserReaction(reviewId, userId, false);
    }

    private void setUserReaction(Long reviewId, Long userId, boolean isLike) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (isLike) {
            reviewStorage.addLikeToReview(reviewId, userId);
        } else {
            reviewStorage.addDislikeToReview(reviewId, userId);
        }
    }

    private void clearUserReaction(Long reviewId, Long userId, boolean isLike) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (isLike) {
            reviewStorage.deleteLikeToReview(reviewId, userId);
        } else {
            reviewStorage.deleteDislikeToReview(reviewId, userId);
        }
    }

    private void reviewValidation(Review review) {
        if (userService.getUser(review.getUserId()) == null) {
            throw new ValidationException("Данные о пользователе отсутствуют в базе данных");
        }
        if (filmService.getFilm(review.getFilmId()) == null) {
            throw new ValidationException("Данные о фильме отсутствуют в базе данных");
        }
        review.setUseful(0L);
    }

    private void checkReviewId(Long reviewId) {
        if (reviewStorage.get(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
    }

    private void checkUserId(Long userId) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}