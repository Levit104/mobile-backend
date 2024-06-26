INSERT INTO users (login, password)
VALUES ('user1', 'password1'),
       ('user2', 'password2'),
       ('user3', 'password3'),
       ('user4', 'password4'),
       ('user5', 'password5'),
       ('user6', 'password6'),
       ('user7', 'password7'),
       ('user8', 'password8'),
       ('user9', 'password9'),
       ('user10', 'password10');

INSERT INTO room (name, user_id)
VALUES ('Гостиная', 1),
       ('Спальня', 2),
       ('Кухня', 3),
       ('Ванная', 4),
       ('Офис', 5),
       ('Гостевая комната', 6),
       ('Столовая', 7),
       ('Гараж', 8),
       ('Балкон', 9),
       ('Сад', 10);

INSERT INTO device_type (name)
VALUES ('Термостат'),
       ('Лампочка'),
       ('Робот-пылесос'),
       ('Умная розетка'),
       ('Умный замок'),
       ('Датчик дыма'),
       ('Датчик двери'),
       ('Датчик окна'),
       ('Датчик движения'),
       ('Датчик протечки воды');

INSERT INTO device (name, type_id, room_id, user_id)
VALUES ('Термостат в гостиной', 1, 1, 1),
       ('Лампочка в спальне', 2, 2, 2),
       ('Робот-пылесос в гостиной', 3, 3, 3),
       ('Умная розетка в ванной', 4, 4, 4),
       ('Умный замок в офисе', 5, 5, 5),
       ('Датчик дыма в гостевой комнате', 6, 6, 6),
       ('Датчик двери в столовой', 7, 7, 7),
       ('Датчик окна в гараже', 8, 8, 8),
       ('Датчик движения на балконе', 9, 9, 9),
       ('Датчик протечки воды в саду', 10, 10, 10);

INSERT INTO notification (device_id, user_id, time, text)
VALUES (1, 1, '2022-10-20 10:15:00', 'Температура слишком высокая'),
       (2, 2, '2022-10-21 11:30:00', 'Лампа включена'),
       (3, 3, '2022-10-22 12:45:00', 'Пылесос включен'),
       (4, 4, '2022-10-23 13:00:00', 'Уборка завершена'),
       (5, 5, '2022-10-24 14:15:00', 'Дверь заперта'),
       (6, 6, '2022-10-25 15:30:00', 'Обнаружен дым'),
       (7, 7, '2022-10-26 16:45:00', 'Дверь открыта'),
       (8, 8, '2022-10-27 17:00:00', 'Окно закрыто'),
       (9, 9, '2022-10-28 18:15:00', 'Обнаружено движение'),
       (10, 10, '2022-10-29 19:30:00', 'Обнаружена утечка воды');

INSERT INTO statistic (device_id, time, water_meter, electricity_meter)
VALUES (1, '2022-10-20 10:15:00', 25.5, 100.0),
       (2, '2022-10-21 11:30:00', 0.0, 50.0),
       (3, '2022-10-22 12:45:00', 0.0, 0.0),
       (4, '2022-10-23 13:00:00', 0.0, 0.0),
       (5, '2022-10-24 14:15:00', 0.0, 0.0),
       (6, '2022-10-25 15:30:00', 0.0, 0.0),
       (7, '2022-10-26 16:45:00', 0.0, 0.0),
       (8, '2022-10-27 17:00:00', 0.0, 0.0),
       (9, '2022-10-28 18:15:00', 0.0, 0.0),
       (10, '2022-10-29 19:30:00', 0.0, 0.0);

INSERT INTO state_type (name, description)
VALUES ('Вкл/Выкл', 'Состояние для устройств, которые можно включить или выключить'),
       ('Температура', 'Состояние для устройств, которые измеряют температуру'),
       ('Уборка', 'Состояние для устройств, которые занимаются уборкой'),
       ('Протечка воды', 'Состояние для устройств, которые обнаруживают утечку воды'),
       ('Заперто/Открыто', 'Состояние для устройств, которые можно запереть или отпереть'),
       ('Открыто/Закрыто', 'Состояние для устройств, которые можно открыть или закрыть'),
       ('Дым', 'Состояние для устройств, которые обнаруживают дым'),
       ('Электричество', 'Состояние для устройств, которые измеряют потребление электроэнергии'),
       ('Вода', 'Состояние для устройств, которые измеряют потребление воды'),
       ('Яркость', 'Состояние для устройств, которые измеряют яркость');

INSERT INTO state (device_id, state_type_id, value)
VALUES (1, 2, '25.5°C'),
       (2, 1, 'Включено'),
       (3, 3, 'Уборка'),
       (4, 1, 'Выключено'),
       (5, 5, 'Заперто'),
       (6, 7, 'Обнаружен дым'),
       (7, 6, 'Дверь открыта'),
       (8, 6, 'Окно закрыто'),
       (9, 3, 'Обнаружено движение'),
       (10, 4, 'Обнаружена утечка воды');

INSERT INTO action_type (state_type_id, description, parameter_mode)
VALUES (1, 'Включить/Выключить', true),
       (2, 'Установить температуру', true),
       (3, 'Отправить уведомление', false),
       (4, 'Запереть/Отпереть', true),
       (5, 'Открыть/Закрыть', true),
       (6, 'Звуковая сигнализация', false),
       (7, 'Уведомить пользователя', false),
       (8, 'Настроить яркость', true),
       (9, 'Переключить режим', true),
       (10, 'Активировать устройство', false);

INSERT INTO action (action_type_id, device_type_id)
VALUES (1, 1),
       (2, 1),
       (3, 3),
       (4, 5),
       (5, 7),
       (6, 6),
       (7, 2),
       (8, 2),
       (9, 4),
       (10, 10);

INSERT INTO condition (description)
VALUES ('Температура выше 30°C'),
       ('Уборка'),
       ('Обнаружен дым'),
       ('Обнаружена утечка воды'),
       ('Устройство выключено'),
       ('Дверь открыта'),
       ('Окно закрыто'),
       ('Уровень яркости ниже 50%'),
       ('Потребление электроэнергии выше 100 кВтч'),
       ('Потребление воды выше 50 литров');

INSERT INTO script (user_id, device_id, condition_id, action_id, condition_value, action_value, active)
VALUES (1, 1, 1, 2, '30°C', 'Установить на 25°C', true),
       (2, 2, 2, 3, 'Уборка завершена', 'Отправить уведомление', true),
       (3, 3, 3, 6, 'Обнаружен дым', 'Звуковая сигнализация', true),
       (4, 4, 4, 7, 'Обнаружена утечка воды', 'Уведомить пользователя', true),
       (5, 5, 5, 1, 'Устройство выключено', 'Включить', true),
       (6, 6, 6, 4, 'Дверь открыта', 'Запереть', true),
       (7, 7, 7, 5, 'Окно закрыто', 'Открыть', true),
       (8, 8, 8, 8, 'Уровень яркости ниже 50%', 'Настроить на 70%', true),
       (9, 9, 9, 9, 'Потребление электроэнергии выше 100 кВтч', 'Переключить на эко-режим', true),
       (10, 10, 10, 10, 'Потребление воды выше 50 литров', 'Активировать датчик', true);