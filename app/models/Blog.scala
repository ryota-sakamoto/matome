package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

case class Blog(id: String, user_id: Int, blog_type_id: Int,  name: String, url: String, notification: Boolean, update_date: Date) {
    def blog_type: String = {
        val blog_type = BlogType.findById(blog_type_id)
        blog_type match {
            case Some(b) => b.name
            case None => ""
        }
    }

    def user_email: String = {
        val user = User.findById(user_id)
        user match {
            case Some(u) => u.email
            case None => ""
        }
    }
}

object Blog extends Model[Blog] {
    val parser = str("id") ~ int("user_id") ~ int("blog_type_id") ~ str("name") ~ str("url") ~ bool("notification") ~ date("update_date")
    val mapper = parser.map {
        case id ~ user_id ~ blog_type_id ~ name ~ url ~ notification ~ update_date => Blog(id, user_id, blog_type_id, name, url, notification, update_date)
    }

    def insert(blog: Blog) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   insert into %s value ({id}, {user_id}, {blog_type_id}, {name}, {url}, {notification}, {update_date})
                """.format(db_name)).on("id" -> blog.id, "user_id" -> blog.user_id, "blog_type_id" -> blog.blog_type_id, "name" -> blog.name, "url" -> blog.url, "notification" -> blog.notification, "update_date" -> blog.update_date).executeUpdate()
        }
    }

    def update(id: String, blog_type_id: Int, name: String, url: String, notification: Boolean) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   update %s blog set name = {name}, url = {url}, blog_type_id = {blog_type_id}, notification = {notification} where id = {id}
                """.format(db_name)).on("id" -> id, "name" -> name, "url" -> url, "blog_type_id" -> blog_type_id, "notification" -> notification).executeUpdate()
        }
    }

    def update(id: String, update_date: Date) = {
        DB.withConnection { implicit c =>
            SQL(
                """
                   update %s blog set update_date = {update_date} where id = {id}
                """.format(db_name)).on("id" -> id, "update_date" -> update_date).executeUpdate()
        }
    }

    def find(user_id: Int): Seq[Blog] = {
        val s = DB.withConnection { implicit c =>
            SQL("""
                select * from blog
                where blog.user_id = {user_id}
                order by update_date desc
                """).on("user_id" -> user_id).as(mapper *)
        }
        s.sortWith { _.name < _.name }
    }

    def findById(id: String, user_id: Int): Option[Blog] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from blog
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