use ebookshop;
  
drop table if exists user_roles;
drop table if exists users;
create table users (
  username char(16) not null,
  password char(41) not null,
  primary key (username)
);
  
create table user_roles (
  username char(16) not null,
  role     varchar(16) not null,
  primary key (username, role),
  foreign key (username) references users (username)
);
  
insert into users values 
  ('user1', password('user1')), 
  ('admin1', password('admin1'));
  
insert into user_roles values
  ('user1', 'user'), 
  ('admin1', 'admin'), 
  ('admin1', 'user');
 
select * from users;
select * from user_roles;