CREATE TABLE team (
                      id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                      name VARCHAR(128) NOT NULL,
                      description VARCHAR(512),
                      manager_id BIGINT NOT NULL,
                      avatar_key VARCHAR(255),
                      created_at timestamptz DEFAULT current_timestamp,
                      updated_at timestamptz DEFAULT current_timestamp,

                      CONSTRAINT fk_team_manager FOREIGN KEY (manager_id) REFERENCES users (id)
);