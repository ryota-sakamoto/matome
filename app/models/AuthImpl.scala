package models

import javax.inject.Inject

import play.api.db.Database
import anorm._
import anorm.SqlParser._

case class Auth(id: Int, user_id: Int)

class AuthImpl @Inject()(db: Database) {
    val parser = int("id") ~ int("user_id")
    val mapper = parser.map {
        case id ~ user_id => Auth(id, user_id)
    }

    def find(key: String): Option[Auth] = {
        db.withConnection { implicit c =>
            SQL(
                """
                  select auth.id, auth.user_id from auth
                  inner join user on user.id = auth.user_id and user.status = 0
                  where auth_key = {key}
                """).on("key" -> key).as(mapper.singleOpt)
        }
    }
}