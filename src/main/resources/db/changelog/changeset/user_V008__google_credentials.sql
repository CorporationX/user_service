CREATE TABLE google_credentials (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    client_id varchar(128) UNIQUE NOT NULL,
    client_email varchar(128) UNIQUE NOT NULL,
    project_id varchar(32) UNIQUE NOT NULL,
    auth_uri varchar(64) UNIQUE NOT NULL,
    token_uri varchar(64) UNIQUE NOT NULL,
    auth_provider_x509_cert_url varchar(64) UNIQUE NOT NULL,
    client_secret varchar(64) UNIQUE NOT NULL,
    redirect_uri varchar(64) UNIQUE NOT NULL,
    javascript_origin varchar(64) UNIQUE NOT NULL
);