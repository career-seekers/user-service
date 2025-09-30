ALTER TABLE mentor_links
    ADD user_id BIGINT;

ALTER TABLE mentor_links
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE mentor_links
    ADD CONSTRAINT uc_mentor_links_user UNIQUE (user_id);

ALTER TABLE mentor_links
    ADD CONSTRAINT FK_MENTOR_LINKS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);