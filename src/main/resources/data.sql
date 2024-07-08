merge into GENRES (id, name)
    values (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

merge into MPA_RATING (id, name)
    values (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

merge into friendship_status (status_id, name)
    VALUES (1, 'Заявка на добавление в друзья подтверждена'),
    (2, 'Заявка на добавление в друзья не подтверждена');