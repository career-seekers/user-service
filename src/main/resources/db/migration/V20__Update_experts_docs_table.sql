ALTER TABLE expert_documents
    ADD consent_to_expert_pdp BOOLEAN DEFAULT TRUE;

ALTER TABLE expert_documents
    ALTER COLUMN consent_to_expert_pdp SET NOT NULL;

ALTER TABLE expert_documents
    DROP COLUMN consent_to_expert_pdp_id;