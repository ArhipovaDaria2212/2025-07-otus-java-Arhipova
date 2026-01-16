
insert into address (id, street) values (nextval('address_seq'), 'AnyStreet');

insert into client (id, name, login, password, role, address_id)
values (
    nextval('client_seq'),
    'tech user',
    'TECH_USER',
    'qwerty',
    'admin',
    currval('address_seq')
);

insert into phone (id, number, client_id)
values (nextval('phone_seq'), '13-555-22', currval('client_seq')),
    (nextval('phone_seq'), '14-666-333', currval('client_seq'));