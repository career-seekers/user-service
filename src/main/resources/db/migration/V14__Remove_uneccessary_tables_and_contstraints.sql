ALTER TABLE child_to_mentor
    DROP CONSTRAINT fk_child_to_mentor_on_child;

ALTER TABLE child_to_mentor
    DROP CONSTRAINT fk_child_to_mentor_on_mentor;

DROP TABLE child_to_mentor CASCADE;