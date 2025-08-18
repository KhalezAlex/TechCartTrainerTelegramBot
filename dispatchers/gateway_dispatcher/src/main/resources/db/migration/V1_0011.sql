CREATE TABLE message_sent (
    id bigserial PRIMARY KEY,
    message_id int8,
    send_message JSONB,
    app_user bigint NOT NULL REFERENCES app_user(telegram_user_id) ON DELETE CASCADE
);