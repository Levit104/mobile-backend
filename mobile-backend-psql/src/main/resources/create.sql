CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   login VARCHAR(32) UNIQUE NOT NULL,
   password VARCHAR(32) NOT NULL
);

CREATE TABLE room (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL,
    user_id INTEGER REFERENCES users(id) NOT NULL
);

CREATE TABLE device_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE device (
    id SERIAL PRIMARY KEY,
    name VARCHAR (32) NOT NULL,
    type_id INTEGER REFERENCES device_type(id) NOT NULL,
    room_id INTEGER REFERENCES room(id),
    user_id INTEGER REFERENCES users(id) NOT NULL
);

CREATE TABLE notification (
    id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES device(id) NOT NULL,
    user_id INTEGER REFERENCES users(id) NOT NULL,
    time TIMESTAMP NOT NULL,
    text VARCHAR(256) NOT NULL
);

CREATE TABLE statistic (
   id SERIAL PRIMARY KEY,
   device_id INTEGER REFERENCES device(id) NOT NULL,
   time TIMESTAMP NOT NULL,
   water_meter REAL,
   electricity_meter REAL
);

CREATE TABLE state_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL,
    description VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE state (
    id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES device(id) NOT NULL,
    state_type_id INTEGER REFERENCES state_type(id) NOT NULL,
    value varchar(32) NOT NULL
);

CREATE TABLE action_type (
    id SERIAL PRIMARY KEY,
    state_type_id INTEGER REFERENCES state_type(id) NOT NULL,
    description VARCHAR(128) UNIQUE NOT NULL,
    parameter_mode BOOLEAN NOT NULL
);

CREATE TABLE action (
    id SERIAL PRIMARY KEY,
    action_type_id INTEGER REFERENCES action_type(id) NOT NULL,
    device_type_id INTEGER REFERENCES device_type(id) NOT NULL
);

CREATE TABLE condition (
    id SERIAL PRIMARY KEY,
    description VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE script (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) NOT NULL,
    device_id INTEGER REFERENCES device(id) NOT NULL,
    condition_id INTEGER REFERENCES condition(id) NOT NULL,
    action_id INTEGER REFERENCES action(id) NOT NULL,
    condition_value VARCHAR(32) NOT NULL,
    action_value VARCHAR(32) NOT NULL,
    active boolean NOT NULL
);

