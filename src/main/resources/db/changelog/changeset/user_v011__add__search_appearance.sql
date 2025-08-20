CREATE TABLE IF NOT EXISTS search_appearance (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    searcher_id bigint NOT NULL,
    searched_id bigint NOT NULL,
    searched_at timestamptz,
    CONSTRAINT fk_searcher_id FOREIGN KEY (searcher_id) REFERENCES users (id),
    CONSTRAINT fk_searched_id FOREIGN KEY (searched_id) REFERENCES users (id)
)