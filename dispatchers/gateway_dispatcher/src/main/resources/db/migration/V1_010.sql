CREATE TABLE test_question
(
    id bigserial PRIMARY KEY,
    variant1 VARCHAR(2000) NOT NULL CHECK (variant1 <> ''),
    variant2 VARCHAR(2000) NOT NULL CHECK (variant2 <> ''),
    variant3 VARCHAR(2000) NOT NULL CHECK (variant3 <> ''),
    variant4 VARCHAR(2000) NOT NULL CHECK (variant4 <> ''),
    item bigint NOT NULL REFERENCES item(id) ON DELETE CASCADE
);