ALTER TABLE child_documents
    ADD age_category SMALLINT DEFAULT 0;

ALTER TABLE child_documents
    ALTER COLUMN age_category SET NOT NULL;