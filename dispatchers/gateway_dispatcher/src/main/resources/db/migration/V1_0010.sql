CREATE TABLE test_result
(
    id bigserial PRIMARY KEY,
    result int4 CHECK ( result BETWEEN 0 AND 100),
    employee bigint NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    category bigint NOT NULL REFERENCES category(id)
);