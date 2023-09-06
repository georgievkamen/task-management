INSERT INTO companies (company_name, address, contact_info)
VALUES ('Company A', 'Address A', 'Contact A'),
       ('Company B', 'Address B', 'Contact B');

INSERT INTO clients (client_name, contact_info)
VALUES ('Client X', 'Contact X'),
       ('Client Y', 'Contact Y');

INSERT INTO projects (title, description, client_id, company_id, deleted)
VALUES ('Project 1', 'Description 1', 1, null, 'false'),
       ('Project 2', 'Description 2', null, 2, 'false');

INSERT INTO tasks (name, status, duration, project_id, deleted)
VALUES ('Task 1', 'NEW', 3600000, 1, 'false'),
       ('Task 2', 'PENDING', 900000, 1, 'false'),
       ('Task 3', 'DONE', 18003132, 2, 'false'),
       ('Task 4', 'FAILED', 54003132, 2, 'false');