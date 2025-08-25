-- src/test/resources/schema.sql
CREATE SCHEMA IF NOT EXISTS winwin;

CREATE TABLE IF NOT EXISTS winwin.users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS winwin.processing_logs (
                                               id UUID PRIMARY KEY,
                                               user_id UUID NOT NULL,
                                               input_text TEXT NOT NULL,
                                               output_text TEXT NOT NULL,
                                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS winwin.user_roles (
                                          user_id UUID NOT NULL,
                                          role VARCHAR(255) NOT NULL,
                                          PRIMARY KEY (user_id, role),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);