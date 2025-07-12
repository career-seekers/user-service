ALTER TABLE user_documents
    DROP COLUMN parent_role;

ALTER TABLE user_documents
    ADD parent_role VARCHAR(255) NOT NULL DEFAULT 'NULL';