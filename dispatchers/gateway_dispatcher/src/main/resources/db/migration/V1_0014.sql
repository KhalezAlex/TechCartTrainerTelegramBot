CREATE PROCEDURE newAppUser(
    p_telegram_user_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO app_user (telegram_user_id, first_login_date, role)
    VALUES (p_telegram_user_id, NOW(), 'GUEST');

    INSERT INTO guest (app_user)
    VALUES (p_telegram_user_id);
END;
$$
