CREATE TABLE department
(
    id bigserial PRIMARY KEY,
    address VARCHAR(150),
    current_view VARCHAR(50),
    state VARCHAR(20) NOT NULL,
    app_user bigint NOT NULL UNIQUE REFERENCES app_user(telegram_user_id) ON DELETE CASCADE,
    company bigint NOT NULL REFERENCES company(id) ON DELETE CASCADE
);