CREATE TABLE item
(
    id bigserial PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weight float8 NOT NULL,
    units VARCHAR(10) NOT NULL,
    category bigint NOT NULL REFERENCES category(id) ON DELETE CASCADE
);