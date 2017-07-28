package controllers

import javax.inject._

import models.Article
import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {
    def index = Action {
        val r = Article.find()
        Ok(views.html.home.index(r))
    }
}
