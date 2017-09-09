# --- !Ups
create table user (
	id int AUTO_INCREMENT,
	email char(255) not null unique,
	name char(15) not null,
	password char(64) not null,
	formal_flag TINYINT default 0,
	primary key(id)
);
create index user_name on user (name);

# --- !Downs
DROP TABLE user;