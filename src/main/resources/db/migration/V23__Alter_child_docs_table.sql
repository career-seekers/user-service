ALTER TABLE child_documents
    DROP CONSTRAINT fk_child_documents_on_user;

ALTER TABLE child_documents
    ADD child_id BIGINT;

ALTER TABLE child_documents
    ALTER COLUMN child_id SET NOT NULL;

ALTER TABLE child_documents
    ADD CONSTRAINT uc_child_documents_child UNIQUE (child_id);

ALTER TABLE child_documents
    ADD CONSTRAINT FK_CHILD_DOCUMENTS_ON_CHILD FOREIGN KEY (child_id) REFERENCES children (id);

ALTER TABLE child_documents
    DROP COLUMN user_id;