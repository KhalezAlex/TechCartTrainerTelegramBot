CREATE TABLE ingredient
(
    id bigserial PRIMARY KEY,
    name VARCHAR(50),
    weight float8,
    units VARCHAR(10),
    item bigint NOT NULL REFERENCES item(id) ON DELETE CASCADE
);