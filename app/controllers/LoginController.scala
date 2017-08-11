package controllers

import javax.inject._

import play.api.mvc._
import play.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import forms.LoginForm
import models.User
import utils.Security

@Singleton
class LoginController @Inject()(cache: CacheApi, val messagesApi: MessagesApi)extends Controller with I18nSupport{
    def index = Action {
        Ok(views.html.login.index(cache, LoginForm.form, null))
    }

    def login = Action { implicit request =>
        val f = LoginForm.form.bindFromRequest

        val user = User.login(f.data("name"), Security.md5(f.data("password")))
        val error = user match {
            case Some(u) => {
                printf("Login success\n")
                null
            }
            case None => {
                printf("Login failed\n")
                "Login failed"
            }
        }

        Ok(views.html.login.index(cache, LoginForm.form, Option(error)))
    }
}
