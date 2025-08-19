ALTER TABLE users
    ADD COLUMN is_mentor BOOLEAN;

UPDATE users
    SET is_mentor = FALSE;

ALTER TABLE users
    ALTER COLUMN is_mentor SET NOT NULL;
