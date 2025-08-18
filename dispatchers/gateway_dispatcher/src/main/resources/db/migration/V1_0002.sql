CREATE TABLE app_user
(
    telegram_user_id bigint PRIMARY KEY,
    first_login_date timestamp,
    username VARCHAR(70),
    role VARCHAR(255)
);