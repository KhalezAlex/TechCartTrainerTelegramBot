CREATE TABLE employee
(
    id bigserial PRIMARY KEY,
    name VARCHAR(70),
    current_view VARCHAR(50),
    state VARCHAR(20) NOT NULL,
    current_test jsonb,
    app_user bigint NOT NULL UNIQUE REFERENCES app_user(telegram_user_id) ON DELETE CASCADE,
    department bigint NOT NULL REFERENCES department(id) ON DELETE CASCADE
);