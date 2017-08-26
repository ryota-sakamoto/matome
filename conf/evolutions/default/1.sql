# --- !Ups
create table blog (
	id char(32) not null,
	user_id int not null,
	blog_type_id int not null,
	name char(30),
	url varchar(100),
	notification tinyint(1) default 0,
	update_date datetime,
	primary key(id)
);

# --- !Downs
DROP TABLE blog;