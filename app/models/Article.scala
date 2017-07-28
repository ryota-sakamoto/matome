package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, Writes}

case class Article(id: Int, blog_id: Int, title: String, url: String, update_date: Date)

object Article extends Model[Article] {
    val parser = int("id") ~ int("blog_id") ~ str("title") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_id ~ title ~ url ~ update_date => Article(id, blog_id, title, url, update_date)
    }

    implicit object ArticleWriter extends Writes[Article] {
        override def writes(article: Article) = {
            Json.toJson(
                Map(
                    "id" -> Json.toJson(article.id),
                    "name" -> Json.toJson(article.title),
                    "url" -> Json.toJson(article.url),
                    "update_date" -> Json.toJson(article.update_date)
                )
            )
        }
    }
}