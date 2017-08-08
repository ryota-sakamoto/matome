package controllers

import javax.inject._

import models.Article
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class HomeController @Inject()(cache: CacheApi) extends Controller {

    def index = Action {
        val r = Article.find()

        Ok(views.html.home.index(cache, r))
    }
}
