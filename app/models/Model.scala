package models

import anorm._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{Json, Writes}

import scala.reflect.ClassTag

trait Model[T] extends ModelClassTag[T]{
    val parser: RowParser[Any]
    val mapper: RowParser[T]

    def find(offset: Int = 0, limit: Int = 10): Seq[T] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from %s
                order by update_date desc
                limit {limit} offset {offset}
                """.format(db_name)).on("limit" -> limit, "offset" -> offset).as(mapper *)
        }
    }

    def findById(id: Int): Option[T] = {
        DB.withConnection { implicit c =>
            SQL("""
                select * from %s where id = {id}
                """.format(db_name)).on("id" -> id).as(mapper.singleOpt)
        }
    }
}

class ModelClassTag[T](implicit class_tag: ClassTag[T]) {
    val db_name: String = class_tag.toString.split('.')(1).toLowerCase
}