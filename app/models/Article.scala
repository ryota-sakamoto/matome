package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Article(id: Int, blog_id: String, title: String, url: String, update_date: Date)

object Article extends Model[Article] {
    val parser = int("id") ~ str("blog_id") ~ str("title") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_id ~ title ~ url ~ update_date => Article(id, blog_id, title, url, update_date)
    }

    // TODO fix and move Model
    def insert(article: Article) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   insert into %s values(null, {blog_id}, {title}, {url}, {update_date})
                """.format(db_name)).on("blog_id" -> article.blog_id, "title" -> article.title, "url" -> article.url, "update_date" -> article.update_date).executeInsert()
        }
    }

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