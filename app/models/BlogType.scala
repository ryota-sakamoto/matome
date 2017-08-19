package models

import anorm._
import anorm.SqlParser._

case class BlogType(id: Int, name: String)

object BlogType extends Model[BlogType] {
    override val db_name = "blog_type"
    val parser = int("id") ~ str("name")
    val mapper = parser.map {
        case id ~ name => BlogType(id, name)
    }
}