package models

import java.util.Date

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Blog(id: Int, blog_type: Int, name: String, description: String, update_date: Date)

object Blog {
    val parser = int("id") ~ int("blog_type") ~ str("name") ~ str("description") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_type ~ name ~ description ~ update_date => Blog(id, blog_type, name, description, update_date)
    }

    def find(offset: Int = 0, limit: Int = 10) = {
        DB.withConnection { implicit c =>
            SQL("select * from blog  where id = {id} ").on("id" -> id).as(mapper *)
        }
    }

    def findById(id: Int): List[Blog] = {
        DB.withConnection { implicit c =>
            SQL("select * from blog  where id = {id} ").on("id" -> id).as(mapper *)
        }
    }
}