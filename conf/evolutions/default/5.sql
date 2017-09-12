# --- !Ups
create table auth (
	id int AUTO_INCREMENT,
	user_id int not null,
	auth_key char(64) not null,
	primary key(id)
);

# --- !Downs
DROP TABLE auth;