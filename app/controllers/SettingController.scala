package controllers

import javax.inject._
import play.api.mvc._

import models.Blog

@Singleton
class SettingController @Inject() extends Controller {
    def index = Action {
      Ok(views.html.settings.index())
    }

    def blogList = Action {
        val b = Blog.find()
        Ok(views.html.settings.blog_list(b))
    }

    def updateBlogList = Action { implicit request =>
        val data = request.body
        Redirect(routes.SettingController.blogList())
    }
}
