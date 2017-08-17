package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Blog(id: String, name: String, url: String, blog_type: String, update_date: Date)

object Blog extends Model[Blog] {
    val parser = str("id") ~ str("name") ~ str("url") ~ str("blog_type") ~ date("update_date")
    val mapper = parser.map {
        case id ~ name ~ url ~ blog_type ~ update_date => Blog(id, name, url, blog_type, update_date)
    }

    // TODO fix
    def update(id: String, name: String) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   update %s blog set name = {name} where id = {id}
                """.format(db_name)).on("id" -> id, "name" -> name).executeUpdate()
        }
    }

    def update(id: String, name: String, update_date: Date) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   update %s blog set update_date = {update_date} where id = {id}
                """.format(db_name)).on("id" -> id, "name" -> name, "update_date" -> update_date).executeUpdate()
        }
    }

    def find(user_id: Int): Seq[Blog] = {
        val s = DB.withConnection { implicit c =>
            SQL("""
                select blog.id, blog.name, blog.url, blog_type.type as blog_type, blog.update_date from blog
                inner join blog_type on blog_type.id = blog.blog_type_id
                where blog.user_id = {user_id}
                order by update_date desc
                """).on("user_id" -> user_id).as(mapper *)
        }
        s.sortWith { _.name < _.name }
    }

    def findById(id: String, user_id: Int): Option[Blog] = {
        DB.withConnection { implicit c =>
            SQL("""
                select blog.id, blog.name, blog.url, blog_type.type as blog_type, blog.update_date from blog
                inner join blog_type on blog_type.id = blog.blog_type_id
                where blog.id = {id} and blog.user_id = {user_id}
                """).on("id" -> id, "user_id" -> user_id).as(mapper.singleOpt)
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