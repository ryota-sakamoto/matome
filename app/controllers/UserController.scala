package controllers

import javax.inject._

import models.{BlogImpl, User}
import utils.Security
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class UserController @Inject()(cache: CacheApi, blog: BlogImpl) extends Controller {
    val prefix = "[UserController]"
    def show(id: String) = Action { implicit request =>
        val user = User.findByName(id)
        val user_uuid = Security.getSessionUUID(request)
        user match {
            case Some(u) => {
                val b = blog.find(u.id)
                Ok(views.html.user.index(cache, user_uuid, b, u))
            }
            case None => NotFound(views.html.template.notfound())
        }
    }

    def rss(id: String) = Action {
        Ok(s"$prefix rss id: $id")
    }
}
