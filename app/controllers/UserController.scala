package controllers

import javax.inject._

import models.{BlogImpl, UserImpl}
import play.api.cache.AsyncCacheApi
import utils.{Security, UserCache}
import play.api.mvc._

@Singleton
class UserController @Inject()(implicit cache: AsyncCacheApi, blog: BlogImpl, user: UserImpl) extends InjectedController {
    val prefix = "[UserController]"
    def show(id: String) = Action { implicit request: Request[AnyContent] =>
        val user_opt = user.findByName(id)
        val user_uuid = Security.getSessionUUID(request)
        user_opt match {
            case Some(u) => {
                val b = blog.findByUserId(u.id)
                Ok(views.html.user.index(UserCache.get(user_uuid), b, u))
            }
            case None => NotFound(views.html.template.notfound("User Not Found"))
        }
    }

    def rss(id: String) = Action {
        Ok(s"$prefix rss id: $id")
    }
}
