package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Blog(id: Int, name: String, url: String, update_date: Date)

object Blog extends Model[Blog] {
    val parser = int("id") ~ str("name") ~ str("url") ~ date("update_date")
    val mapper = parser.map {
        case id ~ name ~ url ~ update_date => Blog(id, name, url, update_date)
    }

    def update(blog: Blog) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   update %s blog set name = {name}, url = {url}, update_date = {update_date} where id = {id}
                """.format(db_name)).on("id" -> blog.id, "name" -> blog.name, "url" -> blog.url, "update_date" -> blog.update_date).executeUpdate()
        }
    }

    override def find(offset: Int = 0, limit: Int = 10): Seq[Blog] = {
        val s = super.find(offset, limit)
        s.sortWith { _.id < _.id }
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