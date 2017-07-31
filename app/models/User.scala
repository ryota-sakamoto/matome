package models

import anorm._
import anorm.SqlParser._

case class User(id:Int)

object User extends Model[User] {
    val parser = int("id")
    val mapper = parser.map {
        case id => User(id)
    }
}