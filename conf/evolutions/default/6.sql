# --- !Ups
create table favorite (
	id int AUTO_INCREMENT,
	user_id int not null,
	article_id char(32) not null,
	primary key(id)
);

# --- !Downs
DROP TABLE favorite;