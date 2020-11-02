CREATE TABLE IF NOT EXISTS ciudadanos (
    id integer,
    name varchar(100),
    address varchar(200),
    email varchar(100),
    valido boolean,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS documentos (
    id varchar(100),
    idCiudadano integer,
    url varchar(500),
    titulo varchar (200),
    autenticado boolean
);
