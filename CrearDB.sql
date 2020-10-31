CREATE DATABASE carpetaciudadana
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

CREATE TABLE ciudadanos (
    id integer,
    name varchar(100),
    address varchar(200),
    email varchar(100),
    valido boolean,
    PRIMARY KEY (id)
);

CREATE TABLE documentos (
    id varchar(100),
    idCiudadano integer,
    url varchar(500),
    titulo varchar (200),
    autenticado boolean
);