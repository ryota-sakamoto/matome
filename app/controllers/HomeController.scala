package controllers

import javax.inject._

import models.Blog
import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {
    def index = Action {
        val r = Blog.findById(1)
        Ok(views.html.home.index(r))
    }
}
