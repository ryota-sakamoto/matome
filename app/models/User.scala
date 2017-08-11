package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class User(id:Int, name: String, password: String)

object User extends Model[User] {
    val parser = int("id") ~ str("name") ~ str("password")
    val mapper = parser.map {
        case id ~ name ~ password => User(id, name, password)
    }

    def login(name: String, password: String): Option[User] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from %s where name = {name} and password = {password}
                """.format(db_name)).on("name" -> name, "password" -> password).as(mapper.singleOpt)
        }
    }
}