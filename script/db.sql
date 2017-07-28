create database matome;

create table blog (
	id int AUTO_INCREMENT,
	name char(30),
	url varchar(30),
	update_date datetime,
	primary key(id)
);

create table article (
	id int AUTO_INCREMENT,
	blog_id int,
	title char(30),
	url char(100),
	update_date datetime,
	primary key(id)
);