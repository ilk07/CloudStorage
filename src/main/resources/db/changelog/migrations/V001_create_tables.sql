--liquibase formatted sql

--changeset ilk07:1.1
create table users
(
    id serial primary key,
    username varchar(100) NOT NULL UNIQUE,
    email varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    status varchar(25) NOT NULL DEFAULT 'ACTIVE',
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
--rollback drop table user;

--changeset ilk07:1.2
create table roles
(
    id serial primary key,
    name varchar(100) NOT NULL,
    status varchar(25) NOT NULL DEFAULT 'ACTIVE',
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
--rollback drop table roles;

--changeset ilk07:1.3
create table user_roles
(
    user_id bigint,
    role_id bigint
);
--rollback drop table user_roles;

--changeset ilk07:1.4
create table user_files
(
    id serial primary key,
    name varchar(255) NOT NULL,
    upload_name varchar(255) NOT NULL,
    file_folder varchar(255),
    file_extension varchar(40) NOT NULL,
    size bigint NOT NULL,
    content_type varchar(150) NOT NULL,
    file_content bytea,
    user_id bigint NOT NULL,
    status varchar(25) NOT NULL DEFAULT 'ACTIVE',
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint fk_files_users
        foreign key (user_id)
            REFERENCES users (id)
);
--rollback drop table user_files;