package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Delete;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.Update;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        log.info("Запрос на сохранение информации об отзыве {} к фильму", review.toString());
        Review createdReview = reviewService.create(review);
        log.info("Информация о новом отзыве {} к фильму сохранена", createdReview.toString());
        return createdReview;
    }

    @PutMapping
    public Review update(@Validated(Update.class) @RequestBody Review newReview) {
        log.info("Запрос на обновление информации об отзыве к фильму: {}", newReview.toString());
        Review updatedReview = reviewService.update(newReview);
        log.info("Информация об отзыве к фильму обновлена: {}", updatedReview.toString());
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Validated(Delete.class) @PathVariable("id") long id) {
        log.info("Запрос на удаление информации об отзыве c id = {} к фильму", id);
        reviewService.delete(id);
        log.info("Информация об отзыве с id = {} к фильму удалена", id);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable("id") long id) {
        log.info("Запрос информации об отзыве c id = {}", id);
        Review review = reviewService.get(id);
        log.info("Получена информация об отзыве c id = {}", id);
        return review;
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(name = "filmId", defaultValue = "-1", required = false) long filmId, Integer count) {
        log.info("Запрос информации обо всех отзывах");
        List<Review> reviews = reviewService.getByFilm(filmId, count);
        log.info("Получена информации обо всех отзывах");
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLikeToReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        log.info("Запрос на добавление лайка к отзыву c id = {} от пользователя с id = {}", reviewId, userId);
        reviewService.addLikeToReview(reviewId, userId);
        log.info("Лайк к отзыву c id = {} от пользователя с id = {} добавлен", reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDislikeToReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        log.info("Запрос на добавление дизлайка к отзыву c id = {} от пользователя с id = {}", reviewId, userId);
        reviewService.addDislikeToReview(reviewId, userId);
        log.info("Дизлайк к отзыву c id = {} от пользователя с id = {} добавлен", reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        log.info("Запрос на удаление лайка к отзыву c id = {} от пользователя с id = {}", reviewId, userId);
        reviewService.deleteLikeToReview(reviewId, userId);
        log.info("Лайк к отзыву c id = {} от пользователя с id = {} удален", reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        log.info("Запрос на удаление дизлайка к отзыву c id = {} от пользователя с id = {}", reviewId, userId);
        reviewService.deleteDislikeToReview(reviewId, userId);
        log.info("Дизлайк к отзыву c id = {} от пользователя с id = {} удален", reviewId, userId);
    }
}