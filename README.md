## java-filmorate
Template repository for Filmorate project.
## ER-диаграмма
```mermaid
erDiagram
    REVIEWS {
        bigint id PK
        text content
        bool is_positive
        bigint user_id FK
        bigint film_id FK
        bigint useful
    }
    
    REVIEW_RATING {
        bigint review_id PK
        bigint user_id PK
        bool is_positive
    }
    
    FILMS {
        bigint id PK
        text name
        varchar(200) description
        date releaseDate
        integer duration
        bigint rating_id FK
        bigint director_id FK
    }

    LIKES {
        bigint user_id FK
        bigint film_id FK
    }
    
    USERS {
        bigint id PK
        text email
        text login
        text name
        date birthday
    }
    
    FRIENDS {
        bigint user1_Id FK
        bigint user2_id FK
        bigint status FK 
    }

    FRIENDSHIP_STATUS {
        bigint status_id PK
        text name
    }

    FEED {
        bigint event_id PK
        bigint user_id FK
        bigint timestamp
        text event_type
        text event_operation
        bigint entity_id
    }

    GENRES {
        bigint id PK
        text name
    }

    FILM_GENRES {
        bigint film_id FK
        bigint genre_id FK
    }
    
    DIRECTORS {
        bigint id PK
        text name
    }

    MPA_RATING {
        bigint id PK
        text name
    }
    
    FILMS || .. o{ LIKES: in
    FILMS || .. || MPA_RATING: in
    LIKES || -- || USERS: in
    FRIENDS }o -- |{ USERS: in
    FRIENDSHIP_STATUS || .. |{ FRIENDS: in
    FILMS || .. |{ FILM_GENRES: in
    FILM_GENRES }o .. o{ GENRES: in
    FILMS || -- o{  DIRECTORS: in
    FILMS || -- o{ REVIEWS: in
    USERS || -- o{ REVIEWS: in
    REVIEWS || -- || REVIEW_RATING: in
    USERS || -- o{ FEED: in
```
#



 

