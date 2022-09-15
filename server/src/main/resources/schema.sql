CREATE TABLE IF NOT EXISTS users
(
    user_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    512
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS requests
(
    request_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    description
    varchar
(
    512
) NOT NULL,
    requester_id BIGINT REFERENCES users
(
    user_id
) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    description VARCHAR
(
    512
) NOT NULL,
    owner BIGINT REFERENCES users
(
    user_id
) NOT NULL,
    available BOOLEAN NOT NULL,
    request_id BIGINT REFERENCES requests
(
    request_id
)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    start_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    booker_id
    BIGINT
    REFERENCES
    users
(
    user_id
) NOT NULL,
    item_id BIGINT REFERENCES items
(
    item_id
) NOT NULL,
    status VARCHAR NOT NULL
    );

CREATE TABLE IF NOT EXISTS comments
(
    comment_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    text
    varchar
(
    512
) NOT NULL,
    user_id BIGINT REFERENCES users
(
    user_id
) NOT NULL,
    item_id BIGINT REFERENCES items
(
    item_id
) NOT NULL,
    creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );