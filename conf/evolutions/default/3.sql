# --- !Ups
create table article (
	id char(32) not null,
	blog_id char(32) not null,
	title char(255),
	url char(100),
	update_date datetime,
	primary key(id)
);
create index article_id on article (id);
create index article_date on article (update_date);

# --- !Downs
DROP TABLE article;