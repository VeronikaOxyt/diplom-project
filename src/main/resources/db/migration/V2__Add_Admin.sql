insert into users (id, username, password, active, fullname)
values (3, 'Referee', '456', true, 'Дмитрий Петров');

insert into user_roles (user_id, roles)
values (3, 'ROLE_USER'), (3, 'ROLE_REFEREE');

insert into users (id, username, password, active, fullname)
values (4, 'Instructor', '789', true, 'Иван Иванов');

insert into user_roles (user_id, roles)
values (4, 'ROLE_USER'), (4, 'ROLE_INSTRUCTOR');