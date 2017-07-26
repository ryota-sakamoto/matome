package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Blog(id: Int, blog_name: String, name: String, description: String, update_date: Date)

object Blog {
    val parser = int("id") ~ str("blog_name") ~ str("name") ~ str("description") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_name ~ name ~ description ~ update_date => Blog(id, blog_name, name, description, update_date)
    }

    def find(offset: Int = 0, limit: Int = 10): Seq[Blog] = {
        DB.withConnection { implicit c =>
            SQL("""
                select blog.*, blog_type.name as blog_name from blog
                inner join blog_type on blog.blog_type_id = blog_type.id
                limit {limit} offset {offset}
                """).on("limit" -> limit, "offset" -> offset).as(mapper *)
        }
    }

    implicit object BlogWriter extends Writes[Blog] {
        override def writes(blog: Blog) = {
            Json.toJson(
                Map(
                    "id" -> Json.toJson(blog.id),
                    "blog_name" -> Json.toJson(blog.blog_name),
                    "name" -> Json.toJson(blog.name),
                    "description" -> Json.toJson(blog.description),
                    "update_date" -> Json.toJson(blog.update_date)
                )
            )
        }
    }
}