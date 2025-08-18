CREATE TABLE guest
(
    id bigserial PRIMARY KEY,
    previous_view VARCHAR(50),
    current_view VARCHAR(50),
    app_user bigint NOT NULL UNIQUE REFERENCES app_user(telegram_user_id) ON DELETE CASCADE
);