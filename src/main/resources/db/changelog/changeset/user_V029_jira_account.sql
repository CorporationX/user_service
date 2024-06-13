CREATE TABLE IF NOT EXISTS jira_account
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id     BIGINT                                  NOT NULL ,
    username    VARCHAR(255)                            UNIQUE NOT NULL,
    password    VARCHAR(255)                            NOT NULL,
    project_url VARCHAR(255)                            NOT NULL,

    CONSTRAINT FK_JIRA_ACCOUNT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);