package models

import java.util.Date
import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database
import play.api.libs.json.{Json, Writes}

case class Article(id: String, blog_id: String, title: String, url: String, update_date: Date)

object Article {
    implicit object ArticleWriter extends Writes[Article] {
        override def writes(article: Article) = {
            Json.toJson(
                Map(
                    "id" -> Json.toJson(article.id),
                    "title" -> Json.toJson(article.title),
                    "url" -> Json.toJson("/articles/%s".format(article.id)), // TODO
                    "update_date" -> Json.toJson(article.update_date)
                )
            )
        }
    }
}

class ArticleImpl @Inject()(db: Database) {
    val parser = str("id") ~ str("blog_id") ~ str("title") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_id ~ title ~ url ~ update_date => Article(id, blog_id, title, url, update_date)
    }

    def find(id: String): Option[Article] = {
        db.withConnection { implicit c =>
            SQL(
                """
                  select * from article where id = {id}
                """).on("id" -> id).as(mapper.singleOpt)
        }
    }

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

    def bulkInsert(articles: Seq[Article]) = {
        val sql = "insert into article values " + articles.map { article =>
            "('%s', '%s', '%s', '%s', FROM_UNIXTIME(%d))".format(
                article.id,
                article.blog_id,
                article.title,
                article.url,
                article.update_date.getTime / 1000
            )
        }.mkString(",")

        db.withConnection { implicit c =>
            SQL(sql).executeInsert()
        }
    }
}