package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.getUserStorage().findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пользователь создан");
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пользователь обновлен");
        return userService.getUserStorage().update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Set<Long> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
       return userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public Set<Long> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
