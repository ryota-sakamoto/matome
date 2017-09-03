package models

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database

case class User(id: Int, name: String, email: String)

class UserImpl @Inject()(db: Database) extends Model[User] {
    val parser = int("id") ~ str("name") ~ str("email")
    val mapper = parser.map {
        case id ~ name ~ email => User(id, name, email)
    }
    val not_formal = 0
    val formal = 1

    def findByName(name: String): Option[User] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from user where name = {name}
            """).on("name" -> name).as(mapper.singleOpt)
        }
    }

    def login(name: String, password: String): Option[User] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from %s where name = {name} and password = {password}
                """.format(db_name)).on("name" -> name, "password" -> password).as(mapper.singleOpt)
        }
    }

    def create(email: String, name: String, password: String, formal_flag: Int): Option[Long] = {
        db.withConnection { implicit c =>
            SQL("""
                insert into %s value (null, {email}, {name}, {password}, {formal_flag})
                """.format(db_name)).on("email" -> email, "name" -> name, "password" -> password, "formal_flag" -> formal_flag).executeInsert()
        }
    }
}