ALTER TABLE tutor_documents
    DROP COLUMN consent_to_tutor_pdp_id;

ALTER TABLE tutor_documents
    ADD consent_to_tutor_pdp_id BOOLEAN NOT NULL DEFAULT TRUE;