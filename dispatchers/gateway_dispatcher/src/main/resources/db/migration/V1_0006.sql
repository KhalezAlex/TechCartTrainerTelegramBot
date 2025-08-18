CREATE TABLE category
(
    id bigserial PRIMARY KEY,
    name VARCHAR(70) NOT NULL,
    department bigint NOT NULL REFERENCES department(id) ON DELETE CASCADE
);