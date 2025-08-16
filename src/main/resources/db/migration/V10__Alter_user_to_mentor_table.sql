ALTER TABLE user_to_mentor
    ALTER COLUMN mentor_id DROP NOT NULL;

ALTER TABLE user_to_mentor
    ALTER COLUMN user_id DROP NOT NULL;