package models

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database

case class BlogType(id: Int, name: String)

class BlogTypeImpl @Inject()(db: Database) extends Model[BlogType] {
    override val db_name = "blog_type"
    val parser = int("id") ~ str("name")
    val mapper = parser.map {
        case id ~ name => BlogType(id, name)
    }

    def list: Seq[BlogType] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from %s
                """.format(db_name)).as(mapper *)
        }
    }
}