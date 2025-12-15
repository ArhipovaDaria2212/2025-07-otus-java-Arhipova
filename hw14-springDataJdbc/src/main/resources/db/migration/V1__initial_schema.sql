
create table client
(
    id          bigserial not null primary key,
    name        varchar(50),
    login       varchar(50),
    password    varchar(50),
    role        varchar(10)
);

create table address
(
    client_id   bigint not null references client (id),
    street      varchar(255)
);

create table phone
(
    id          bigserial not null primary key,
    number      varchar(15),
    client_key  int,
    client_id   bigint references client (id)
);