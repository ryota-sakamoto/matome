package controllers

import javax.inject._

import actions.AuthAction
import play.api.mvc._
import models.Blog
import play.cache.CacheApi
import utils.{Security, UserCache}

@Singleton
class SettingController @Inject()(cache: CacheApi) extends Controller {
    def index = Action {
      Ok(views.html.settings.index())
    }

    def blogList = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(cache, user_uuid)

            val b = Blog.find(user.get.id)
            Ok(views.html.settings.blog_list(cache, user_uuid, b))
        })

    def blogEdit(id: String) = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(cache, user_uuid)

            val blog = Blog.findById(id, user.get.id)
            blog match {
                case Some(b) => Ok(b.toString)
                case None => NotFound("Not Found")
            }
        }
    )

    // TODO use form and refactoring
    def blogUpdate(id: String) = AuthAction( cache,
        Action { implicit request =>
            Redirect(routes.SettingController.blogList())
        }
    )
}
