ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS messages_sent JSONB,
    ADD COLUMN IF NOT EXISTS message_ids_to_delete JSONB;