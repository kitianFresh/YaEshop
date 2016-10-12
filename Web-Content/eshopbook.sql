create database if not exists ebookshop;
 
use ebookshop;
 
drop table if exists books;
create table books (
  id     int,
  title  varchar(50),
  author varchar(50),
  price  float,
  qty    int,
  primary key (id));
 
insert into books values (1001, 'Java for dummies', 'Tan Ah Teck', 11.11, 11);
insert into books values (1002, 'More Java for dummies', 'Tan Ah Teck', 22.22, 22);
insert into books values (1003, 'More Java for more dummies', 'Mohammad Ali', 33.33, 33);
insert into books values (1004, 'A Cup of Java', 'Kumar', 44.44, 44);
insert into books values (1005, 'A Teaspoon of Java', 'Kevin Jones', 55.55, 55);
 
drop table if exists order_records;
create table order_records (
  id          int,
  qty_ordered int,
  cust_name   varchar(30),
  cust_email  varchar(30),
  cust_phone  char(8));
 
select * from books;
  
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