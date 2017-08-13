package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class UserController @Inject() extends Controller {
    val prefix = "[UserController]"
    def show(id: String) = Action {
        Ok(s"$prefix id: $id")
    }

    def rss(id: String) = Action {
        Ok(s"$prefix rss id: $id")
    }
}
