ALTER TABLE user_documents
    ADD birth_certificate_id BIGINT DEFAULT 0;

ALTER TABLE user_documents
    ALTER COLUMN birth_certificate_id SET NOT NULL;

ALTER TABLE user_documents
    ADD CONSTRAINT uc_user_documents_birthcertificateid UNIQUE (birth_certificate_id);