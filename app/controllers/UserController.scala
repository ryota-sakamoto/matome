package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class UserController @Inject() extends Controller {
    def show(id: String) = Action {
        Ok(id)
    }
}
