package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserStorage.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
public class DbUserStorageTest {
    private final UserStorage userStorage;


    @Test
    void create() {
        userStorage.create(new User(
                "mail@mail.ru",
                "dolore",
                "Nick Name",
                LocalDate.of(2014, 4, 17)
        ));

        List<User> users = userStorage.findAll();
        assertEquals(1, users.size());
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "mail@mail.ru");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("login", "dolore");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name", "Nick Name");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2014, 4, 17));
    }

    @Test
    @Sql(scripts = {"/tests-get-users.sql"})
    void update() {
        userStorage.update(new User(
                1L,
                "newEmail1@email.ru",
                "newLogin1",
                "newName1",
                LocalDate.of(2014, 4, 17)
        ));

        User user = userStorage.getUser(1L);
        assertThat(user).hasFieldOrPropertyWithValue("email", "newEmail1@email.ru");
        assertThat(user).hasFieldOrPropertyWithValue("login", "newLogin1");
        assertThat(user).hasFieldOrPropertyWithValue("name", "newName1");
        assertThat(user).hasFieldOrProperty("birthday");
    }

    @Test
    @Sql(scripts = {"/tests-get-users.sql"})
    void getUser() {
        User user = userStorage.getUser(1L);

        assertThat(user).hasFieldOrPropertyWithValue("email", "email1@email.ru");
        assertThat(user).hasFieldOrPropertyWithValue("login", "login1");
        assertThat(user).hasFieldOrPropertyWithValue("name", "name1");
        assertThat(user).hasFieldOrProperty("birthday");
    }

    @Test
    @Sql(scripts = {"/tests-get-users.sql"})
    void delete() {
        userStorage.delete(1L);

        assertEquals(1, userStorage.findAll().size());
    }

    @Test
    @Sql(scripts = {"/tests-get-users.sql"})
    void findAll() {
        List<User> users = userStorage.findAll();

        assertEquals(2, users.size());

        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "email1@email.ru");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("login", "login1");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name", "name1");
        assertThat(users.get(0)).hasFieldOrProperty("birthday");

        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("email", "email2@email.ru");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("login", "login2");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("name", "name2");
        assertThat(users.get(1)).hasFieldOrProperty("birthday");
    }
}
