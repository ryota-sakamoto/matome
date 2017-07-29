package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}
import utils.Validation

case class Blog(id: Int, name: String, url: String, update_date: Date)

object Blog extends Model[Blog] {
    val parser = int("id") ~ str("name") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ name ~ url ~ update_date => Blog(id, name, url, update_date)
    }

    // TODO fix and move Model
    def delete() = {
        DB.withConnection {implicit c =>
            SQL("delete from %s".format(db_name)).executeUpdate()
        }
    }

    // TODO fix and move Model
    def insert(blog: Blog) = {
        if (Validation.isURL(blog.url)) {
            DB.withConnection { implicit c =>
                SQL(
                    """
                   insert into %s value({id}, {name}, {url}, now())
                """.format(db_name)).on("id" -> blog.id, "name" -> blog.name, "url" -> blog.url).executeInsert()
            }
        }
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