package controllers

import javax.inject._

import utils.{Security, UserCache}
import play.api.mvc._
import play.api.cache.AsyncCacheApi

@Singleton
class HomeController @Inject()(implicit cache: AsyncCacheApi) extends InjectedController {

    def index = Action { implicit request: Request[AnyContent] =>
        val user_uuid = Security.getSessionUUID(request)
        val user = UserCache.get(user_uuid)

        Ok(views.html.home.index(user))
    }
}
