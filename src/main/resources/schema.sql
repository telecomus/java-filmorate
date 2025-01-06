-- Таблица рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id INTEGER REFERENCES mpa_ratings(mpa_id)
);

-- Таблица связи фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

-- Таблица лайков
CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

-- Таблица дружбы (односторонняя)
CREATE TABLE IF NOT EXISTS friendship (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

-- Заполнение таблицы рейтингов MPA
MERGE INTO mpa_ratings (mpa_id, name) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

-- Заполнение таблицы жанров
MERGE INTO genres (genre_id, name) VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');