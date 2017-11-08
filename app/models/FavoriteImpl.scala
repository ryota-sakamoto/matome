package models

import javax.inject.Inject
import play.api.db.Database
import anorm._

case class Favorite(user_id: Int, article_id: String)

class FavoriteImpl @Inject()(db: Database) {
    def insert(favorite: Favorite): Int = {
        db.withConnection { implicit c =>
            SQL(
                """
                  insert into favorite value (null, {usr_id}, {article_id})
                """).on("user_id" -> favorite.user_id, "article_id" -> favorite.article_id).executeUpdate()
        }
    }
}
