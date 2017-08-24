package controllers

import javax.inject._

import models.Blog
import utils.Security
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class HomeController @Inject()(cache: CacheApi) extends Controller {

    def index = Action { implicit request =>
        val user_uuid = Security.getSessionUUID(request)

        val blog = Blog.find()

        Ok(views.html.home.index(cache, user_uuid, blog))
    }
}
