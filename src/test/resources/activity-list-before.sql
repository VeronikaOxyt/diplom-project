delete from activity_participant;
delete from activity;

insert into activity(id, text, type, location, start_date_time, finish_date_time, user_id) values
(153, 'Турнир', 'Турнир', 'Рязань, Геккон', '2024-03-25 11:00:00.000', '2024-03-25 18:00:00.000', 103),
(154, 'Выезд', 'Выезд', 'Воргольские скалы', '2024-04-05 09:00:00.000', '2024-04-08 16:00:00.000', 104),
(155, 'Мастер-класс', 'Мастер-класс', 'Рязань, Геккон', '2024-04-25 11:00:00.000', '2024-04-25 18:00:00.000', 103);

alter sequence hibernate_sequence restart with 180