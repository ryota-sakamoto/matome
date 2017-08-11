package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class User(id:Int, name: String)

object User extends Model[User] {
    val parser = int("id") ~ str("name")
    val mapper = parser.map {
        case id ~ name => User(id, name)
    }
    val not_formal = 0
    val formal = 1

    def login(name: String, password: String): Option[User] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from %s where name = {name} and password = {password}
                """.format(db_name)).on("name" -> name, "password" -> password).as(mapper.singleOpt)
        }
    }

    def create(email: String, name: String, password: String, formal_flag: Int): Option[Long] = {
        DB.withConnection { implicit c =>
            SQL("""
                insert into %s value (null, {email}, {name}, {password}, {formal_flag})
                """.format(db_name)).on("email" -> email, "name" -> name, "password" -> password, "formal_flag" -> formal_flag).executeInsert()
        }
    }
}