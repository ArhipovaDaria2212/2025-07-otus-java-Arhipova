
insert into client (name, login, password, role)
values ('tech user', 'TECH_USER', 'qwerty', 'admin');

insert into address (client_id, street)
select id, 'AnyStreet'
from client
where login = 'TECH_USER';

insert into phone (number, client_id)
select '13-555-22', id
from client
where login = 'TECH_USER';