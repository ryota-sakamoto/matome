package models

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database

case class BlogType(id: Int, name: String)

class BlogTypeImpl @Inject()(db: Database) {
    val parser = int("id") ~ str("name")
    val mapper = parser.map {
        case id ~ name => BlogType(id, name)
    }

    def list: Seq[BlogType] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from blog_type
                """).as(mapper *)
        }
    }

    def findById(id: Int): Option[BlogType] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from blog_type where id = {id}
                """).on("id" -> id).as(mapper.singleOpt)
        }
    }
}