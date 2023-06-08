--liquibase formatted sql

--changeset ilk07:1.8
insert into users (username, email, password) values ('user@user.com', 'user@user.com','$2a$12$jNALp2NMAeZHAZZvMyXVJerfpL7sBMlny/iL5IL4NipKTOzLlBoZm');
insert into users (username, email, password) values ('test@test.com', 'test@test.com','$2a$12$jNALp2NMAeZHAZZvMyXVJerfpL7sBMlny/iL5IL4NipKTOzLlBoZm');
--rollback truncate table users;

--changeset ilk07:1.12
insert into user_roles (user_id, role_id) values (1,1);
insert into user_roles (user_id, role_id) values (2,1);
--rollback truncate table user_roles;


