CREATE SCHEMA IF NOT EXISTS winwin;

CREATE TABLE winwin.users (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              username VARCHAR(255) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL
);

-- Змініть processing_log на processing_logs
CREATE TABLE winwin.processing_logs (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        user_id UUID NOT NULL REFERENCES winwin.users(id) ON DELETE CASCADE,
                                        input_text TEXT NOT NULL,
                                        output_text TEXT NOT NULL,
                                        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE winwin.user_roles (
                                   user_id UUID NOT NULL,
                                   role VARCHAR(255) NOT NULL,
                                   PRIMARY KEY (user_id, role),
                                   CONSTRAINT fk_user_roles_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES winwin.users (id)
                                           ON DELETE CASCADE
);