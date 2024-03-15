create sequence hibernate_sequence start 2 increment 1;

create table activity (
    id int8 not null,
    filename varchar(255),
    text varchar(2048) not null,
    type varchar(255) not null,
    user_id int8,
    primary key (id)
);

create table user_roles (
    user_id int8 not null,
    roles varchar(255)
);

create table users (
    id int8 not null,
    activation_code varchar(255),
    active boolean not null,
    email varchar(255),
    fullname varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

create table activity_participant (
    user_id int8 not null references users,
    activity_id int8 not null references activity,
    primary key (activity_id, user_id)
);