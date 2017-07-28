package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Blog(id: Int, name: String, url: String, update_date: Date)

object Blog {
    val parser = int("id") ~ str("name") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ name ~ url ~ update_date => Blog(id, name, url, update_date)
    }

    implicit object BlogWriter extends Writes[Blog] {
        override def writes(blog: Blog) = {
            Json.toJson(
                Map(
                    "id" -> Json.toJson(blog.id),
                    "name" -> Json.toJson(blog.name),
                    "url" -> Json.toJson(blog.url),
                    "update_date" -> Json.toJson(blog.update_date)
                )
            )
        }
    }
}