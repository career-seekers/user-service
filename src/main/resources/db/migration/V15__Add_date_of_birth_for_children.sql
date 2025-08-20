ALTER TABLE children
    ADD date_of_birth date;

ALTER TABLE children
    ALTER COLUMN date_of_birth SET NOT NULL;