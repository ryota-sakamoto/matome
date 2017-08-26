# --- !Ups
create table blog_type (
	id int AUTO_INCREMENT,
	name char(255),
	primary key(id)
);
insert into blog_type values (1, "hatena") (2, "livedoor") (3, "qiita")

# --- !Downs
DROP TABLE blog_type;