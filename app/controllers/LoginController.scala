package controllers

import javax.inject._

import play.api.mvc._
import play.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.Logger
import forms.{LoginForm, RegisterForm}
import models.User
import utils.Security

@Singleton
class LoginController @Inject()(cache: CacheApi, val messagesApi: MessagesApi)extends Controller with I18nSupport {
    def index = Action {
        Ok(views.html.login.index(cache, "", LoginForm.form, RegisterForm.form, null))
    }

    def login = Action { implicit request =>
        val f = LoginForm.form.bindFromRequest

        val user = User.login(f.data("name"), Security.md5(f.data("password")))
        user match {
            case Some(u) => {
                Logger.info("Login success id: %d".format(u.id))
                val uuid = Security.generateUUID()
                cache.set(uuid, u)
                Redirect(routes.HomeController.index()).withSession(Security.session_name -> uuid)
            }
            case None => {
                Logger.info("Login Failed")
                Ok(views.html.login.index(cache, "", LoginForm.form, RegisterForm.form, Option("Login failed")))
            }
        }
    }

    def logout = Action { implicit request =>
        val user_uuid = Security.getSessionUUID(request)
        cache.remove(Security.session_name)

        Redirect(routes.LoginController.index()).withSession(request.session - user_uuid)
    }
}
