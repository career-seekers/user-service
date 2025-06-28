CREATE SEQUENCE IF NOT EXISTS files_storage_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE files_storage
(
    id                BIGINT       NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   UUID         NOT NULL,
    content_type      VARCHAR(255) NOT NULL,
    size              BIGINT       NOT NULL,
    file_type         SMALLINT     NOT NULL,
    file_path         TEXT         NOT NULL,
    CONSTRAINT pk_files_storage PRIMARY KEY (id)
);

ALTER TABLE files_storage
    ADD CONSTRAINT uc_files_storage_storedfilename UNIQUE (stored_filename);