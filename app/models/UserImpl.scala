package models

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import play.api.db.Database

case class User(id: Int, name: String, email: String)

class UserImpl @Inject()(db: Database) {
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

    def findById(id: Int): Option[User] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from user where id = {id}
            """).on("id" -> id).as(mapper.singleOpt)
        }
    }

    def login(name: String, password: String): Option[User] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from user where name = {name} and password = {password}
                """).on("name" -> name, "password" -> password).as(mapper.singleOpt)
        }
    }

    def create(email: String, name: String, password: String, formal_flag: Int): Option[Long] = {
        db.withConnection { implicit c =>
            SQL("""
                insert into user value (null, {email}, {name}, {password}, {formal_flag})
                """).on("email" -> email, "name" -> name, "password" -> password, "formal_flag" -> formal_flag).executeInsert()
        }
    }

    def update(new_user: User): Int = {
        db.withConnection { implicit c =>
            SQL("""
                update user set name = {name}, email = {email} where id = {id}
                """).on("id" -> new_user.id, "name" -> new_user.name, "email" -> new_user.email).executeUpdate()
        }
    }

    def checkExists(name: String): Boolean = {
        val user_opt = findByName(name)

        user_opt match {
            case Some(u) => true
            case None => false
        }
    }
}