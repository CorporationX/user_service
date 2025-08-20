CREATE TABLE IF NOT EXISTS profile_visits (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    visitor_id bigint NOT NULL,
    visited_id bigint NOT NULL,
    visited_at timestamptz,
    CONSTRAINT fk_visitor_id FOREIGN KEY (visitor_id) REFERENCES users (id),
    CONSTRAINT fk_visited_id FOREIGN KEY (visited_id) REFERENCES users (id)
);