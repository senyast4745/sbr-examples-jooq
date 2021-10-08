create table USERS
(
    id       BIGSERIAL PRIMARY KEY,
    login    varchar(255) UNIQUE,
    password varchar(255)
);