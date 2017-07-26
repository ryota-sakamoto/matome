package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

class Blog {
    val parser = int("id") ~ int("blog_type") ~ str("name") ~ str("description") ~ date("update_date")
    val mapper = parser.map {
        case id ~ blog_type ~ name ~ description ~ update_date => Map("id" -> id, "blog_type" -> blog_type, "name" -> name, "description" -> description, "update_date" -> update_date)
    }

    def test(): List[Map[String, Any]] = {
        DB.withConnection { implicit c =>
            SQL("select * from blog").as(mapper *)
        }
    }
}