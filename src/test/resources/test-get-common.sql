MERGE INTO films (film_id, name, description, releaseDate, duration, rating_id)
VALUES
(1, 'film_name1', 'description1', '2020-1-1', 90, 1),
(2, 'film_name2', 'description2', '2020-1-1', 91, 1),
(3, 'film_name3', 'description3', '2020-1-1', 92, 1),
(4, 'film_name4', 'description4', '2020-1-1', 93, 1),
(5, 'film_name5', 'description5', '2020-1-1', 94, 1),
(6, 'film_name6', 'description6', '2020-1-1', 95, 1);

MERGE INTO users (id, email, login, name, birthday)
VALUES
(1, 'email1@email.ru', 'login1','name1', '2001-01-01'),
(2, 'email2@email.ru', 'login2','name2', '2002-01-01');

DELETE FROM Likes;

INSERT INTO Likes (user_id, film_id)
VALUES (1,1),(1,2),(1,3),(1,4),(2,3),(2,4),(2,5),(2,6);
