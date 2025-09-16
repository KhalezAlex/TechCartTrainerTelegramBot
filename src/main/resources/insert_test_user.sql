-- SQL script to insert related entities
-- All entities are linked to one main app_user

-- 1. Insert main app_user
INSERT INTO app_user (telegram_user_id, first_login_date, username, role)
VALUES (292005725, NOW(), 'main_company_user', 'DEPARTMENT');

-- 2. Insert company linked to the app_user
INSERT INTO company (name, email, current_view, app_user)
VALUES ('TestCompany', 'klozevitz@yandex.ru', 'nullable_view', 292005725);

-- Get the company ID for further references
-- Assuming the company ID will be 1 (since it's the first insert)

-- 3. Insert department linked to the same app_user and company
INSERT INTO department (address, current_view, state, app_user, company)
VALUES ('', 'DEPARTMENT_VIEW', 'PAID', 292005725, 1);

-- Then insert employee (assuming employee table structure)
-- Note: Adjust this according to your actual employee table structure
INSERT INTO employee (name, state, current_view, app_user, department)
VALUES ('Алексей', 'PAID', 'nullable_view', 292005725, 1);

-- Alternative: if employee uses the same app_user as department (less likely but possible)
-- INSERT INTO employee (first_name, last_name, position, app_user, department)
-- VALUES ('John', 'Doe', 'Manager', 123456789, 1);