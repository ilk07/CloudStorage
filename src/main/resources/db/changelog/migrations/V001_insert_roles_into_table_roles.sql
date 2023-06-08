--liquibase formatted sql

--changeset ilk07:1.7
insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ADMIN');
--rollback truncate table roles;