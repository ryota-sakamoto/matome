package models

import anorm._
import anorm.SqlParser._

case class User(id:Int, name: String, password: String)

object User extends Model[User] {
    val parser = int("id") ~ str("name") ~ str("password")
    val mapper = parser.map {
        case id ~ name ~ password => User(id, name, password)
    }
}