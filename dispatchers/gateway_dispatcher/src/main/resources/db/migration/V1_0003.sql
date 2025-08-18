CREATE TABLE company
(
    id bigserial PRIMARY KEY,
    name VARCHAR(70) NOT NULL,
    email VARCHAR(70) NOT NULL,
    current_view VARCHAR(50),
    state VARCHAR(20) NOT NULL,
    app_user bigint NOT NULL UNIQUE REFERENCES app_user(telegram_user_id) ON DELETE CASCADE
);