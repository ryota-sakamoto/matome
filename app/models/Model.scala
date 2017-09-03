package models

import anorm._
import play.api.db.Database

abstract class Model[T](db: Database) {
    val parser: RowParser[Any]
    val mapper: RowParser[T]
    val db_name: String

    def find(offset: Int = 0, limit: Int = 10): Seq[T] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from %s
                order by update_date desc
                limit {limit} offset {offset}
                """.format(db_name)).on("limit" -> limit, "offset" -> offset).as(mapper *)
        }
    }

    def findById(id: Int): Option[T] = {
        db.withConnection { implicit c =>
            SQL("""
                select * from %s where id = {id}
                """.format(db_name)).on("id" -> id).as(mapper.singleOpt)
        }
    }
}