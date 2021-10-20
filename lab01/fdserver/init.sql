CREATE TABLE users (
    usedId serial PRIMARY KEY,
    username VARCHAR (200) UNIQUE NOT NULL,
    password VARCHAR (200) NOT NULL
);