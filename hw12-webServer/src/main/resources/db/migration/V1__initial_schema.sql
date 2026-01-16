
create sequence address_seq start with 1 increment by 1;
create table address
(
    id          bigint not null primary key default nextval('address_seq'),
    street      varchar(255)
);

create sequence client_seq start with 1 increment by 1;
create table client
(
    id          bigint not null primary key default nextval('client_seq'),
    name        varchar(50),
    login       varchar(50),
    role        varchar(10),
    password    varchar(50),
    address_id  bigint references address (id)
);

create sequence phone_seq start with 1 increment by 1;
create table phone
(
    id bigint   not null primary key default nextval('phone_seq'),
    number      varchar(15),
    client_id   bigint references client (id)
);