
create table if not exists USERS
(
    id  bigint  primary key auto_increment,
    email    text not null,
    login    text not null,
    name     text,
    birthday date
);

create table if not exists FRIENDSHIP_STATUS
(
    status_id bigint primary key,
    name      text
);

create table if not exists FRIENDS
(
    user1_id bigint references USERS (id) ON DELETE CASCADE,
    user2_id   bigint references USERS (id) ON DELETE CASCADE,
    status bigint references FRIENDSHIP_STATUS (status_id) ON DELETE CASCADE

);

create table if not exists GENRES
(
    id   bigint primary key,
    name text
);

create table if not exists MPA_RATING
(
    id   bigint primary key,
    name text
);
create table if not exists DIRECTORS
(
    id   bigint primary key auto_increment,
    name text
);

create table if not exists FILMS
(
    film_id     bigint primary key auto_increment,
    name        text not null,
    description text,
    releaseDate date,
    duration    integer check (duration > 0),
    rating_id   bigint references MPA_RATING(id) ON DELETE CASCADE
);

create table if not exists LIKES
(
    user_id bigint references USERS (id) ON DELETE CASCADE,
    film_id bigint references FILMS (film_id) ON DELETE CASCADE
);

create table if not exists FILM_GENRES
(
    film_id  bigint references FILMS (film_id) ON DELETE CASCADE,
    genre_id bigint references GENRES (id) ON DELETE CASCADE
);

create table if not exists FILM_DIRECTORS
(
    film_id  bigint references FILMS (film_id) ON DELETE CASCADE,
    director_id bigint references DIRECTORS (id) ON DELETE CASCADE
    );




