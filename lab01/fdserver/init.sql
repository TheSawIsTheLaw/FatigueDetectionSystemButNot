CREATE TABLE users (
    userId serial PRIMARY KEY,
    username VARCHAR (200) UNIQUE NOT NULL,
    password VARCHAR (200) NOT NULL,
    dbToken VARCHAR (100) NOT NULL
);
