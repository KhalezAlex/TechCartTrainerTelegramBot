CREATE TYPE role_type
AS ENUM ('GUEST', 'COMPANY', 'DEPARTMENT', 'EMPLOYEE', 'ADMIN');

ALTER TABLE
    app_user
    ALTER COLUMN
        "current_role"
        TYPE
        role_type
        USING
        "current_role"::role_type;