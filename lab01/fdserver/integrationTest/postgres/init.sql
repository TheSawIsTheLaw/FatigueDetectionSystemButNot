CREATE TABLE users (
    userId serial PRIMARY KEY,
    username VARCHAR (200) UNIQUE NOT NULL,
    password VARCHAR (200) NOT NULL,
    dbToken VARCHAR (100) NOT NULL
);

INSERT INTO users(username, password, dbtoken) VALUES
    ('testUser', 'password', 'HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow==');