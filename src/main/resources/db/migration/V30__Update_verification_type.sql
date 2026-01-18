ALTER TABLE users
    ADD COLUMN verified_status SMALLINT NOT NULL DEFAULT 2;

UPDATE users
SET verified_status = CASE
                          WHEN verified = true THEN 0
                          ELSE 2
    END;

ALTER TABLE users
    DROP COLUMN verified;

ALTER TABLE users
    RENAME COLUMN verified_status TO verified;

ALTER TABLE users
    ADD CONSTRAINT chk_users_verified_status
        CHECK (verified IN (0, 1, 2));