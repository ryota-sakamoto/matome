package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class BlogType(id: Int, name: String)

object BlogType extends Model[BlogType] {
    override val db_name = "blog_type"
    val parser = int("id") ~ str("name")
    val mapper = parser.map {
        case id ~ name => BlogType(id, name)
    }

    def list: Seq[BlogType] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from %s
                """.format(db_name)).as(mapper *)
        }
    }
}