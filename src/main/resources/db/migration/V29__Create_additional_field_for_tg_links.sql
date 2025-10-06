ALTER TABLE users
    ADD link_id BIGINT;

UPDATE users
SET link_id = tg.id
FROM telegram_links tg
WHERE tg.user_id = users.id;
