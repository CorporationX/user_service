CREATE TABLE jira_accounts (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    username    VARCHAR UNIQUE  NOT NULL,
    password    VARCHAR         NOT NULL,
    project_url VARCHAR         NOT NULL,

    CONSTRAINT fk_user_jira_acc_id FOREIGN KEY (user_id) REFERENCES users (id)
);