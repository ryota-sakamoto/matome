package models

import java.util.Date
import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database
import play.api.libs.json.{Json, Writes}

case class Article(id: Int, blog_id: String, title: String, url: String, update_date: Date)

object Article {
    implicit object ArticleWriter extends Writes[Article] {
        override def writes(article: Article) = {
            Json.toJson(
                Map(
                    "title" -> Json.toJson(article.title),
                    "url" -> Json.toJson(article.url),
                    "update_date" -> Json.toJson(article.update_date)
                )
            )
        }
    }
}

class ArticleImpl @Inject()(db: Database) extends Model[Article](db) {
    val parser = int("id") ~ str("blog_id") ~ str("title") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_id ~ title ~ url ~ update_date => Article(id, blog_id, title, url, update_date)
    }
    val db_name = "article"

    def findByBlogId(blog_id: String, limit: Int, offset: Int): Seq[Article] = {
        db.withConnection { implicit c =>
            SQL(
                """
                  select article.* from article
                  inner join blog on blog.id = article.blog_id
                  where blog_id = {blog_id}
                  order by update_date desc
                  limit {limit} offset {offset}
                """).on("blog_id" -> blog_id, "limit" -> limit, "offset" -> offset).as(mapper *)
        }
    }

    def insert(article: Article) = {
        db.withConnection { implicit c =>
            SQL(
                """
                   insert into %s values(null, {blog_id}, {title}, {url}, {update_date})
                """.format(db_name)).on("blog_id" -> article.blog_id, "title" -> article.title, "url" -> article.url, "update_date" -> article.update_date).executeInsert()
        }
    }
}