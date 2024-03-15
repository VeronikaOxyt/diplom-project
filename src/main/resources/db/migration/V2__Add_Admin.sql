insert into users (id, username, password, active)
values (3, 'Referee', '456', true);

insert into user_roles (user_id, roles)
values (3, 'ROLE_USER'), (3, 'ROLE_REFEREE');

insert into users (id, username, password, active)
values (4, 'Instructor', '789', true);

insert into user_roles (user_id, roles)
values (4, 'ROLE_USER'), (4, 'ROLE_INSTRUCTOR');