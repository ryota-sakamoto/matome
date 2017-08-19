create database matome;

create table blog (
	id char(32) not null,
	user_id int not null,
	blog_type_id int not null,
	name char(30),
	url varchar(100),
	update_date datetime,
	primary key(id)
);

create table blog_type (
	id int AUTO_INCREMENT,
	name char(255),
	primary key(id)
);

create table article (
	id int AUTO_INCREMENT,
	blog_id char(32) not null,
	title char(255),
	url char(100),
	update_date datetime,
	primary key(id)
);
create index article_date on article (update_date);

create table user (
	id int AUTO_INCREMENT,
	email char(255) not null unique,
	name char(15) not null,
	password char(32) not null,
	formal_flag TINYINT default 0,
	primary key(id)
);