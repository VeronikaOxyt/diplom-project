delete from activity_participant;
delete from diplomdbtest.public.user_roles;
delete from diplomdbtest.public.users;


insert into users(id, active, password, username) values
(102, true, '$2a$08$bKwMFQHRqvzTOttEIrAb9.4jE7GWkgyh32JulYHgXRlQZC0quZi3a', 'oxyt'),
(103, true, '$2a$08$lo2yNYkdiXwWl53UX1qn6.6JddAJYJ7HbGZ3daFA0NKdaDB86ZoKe', 'referee'),
(104, true, '$2a$08$zuzl5niY1kL0r/e125fURuXm1MOg9jbVpzB1oAATIu/PW.XPUvf6K', 'instructor');

insert into user_roles(user_id, roles) values
(102, 'ROLE_USER'),
(103, 'ROLE_USER'), (103, 'ROLE_REFEREE'),
(104, 'ROLE_USER'), (104, 'ROLE_INSTRUCTOR');