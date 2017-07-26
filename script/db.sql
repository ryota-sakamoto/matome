create database matome;

create table blog (
  id int AUTO_INCREMENT,
	blog_type_id int,
	name char(30),
	description varchar(100),
	update_date datetime,
	primary key(id)
);

create table blog_type (
  id int AUTO_INCREMENT,
	name char(10),
	primary key(id)
);