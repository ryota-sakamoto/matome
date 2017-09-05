package controllers

import javax.inject._

import play.api.mvc._
import play.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.Logger
import forms.{LoginForm, RegisterForm}
import models.UserImpl
import utils.{Security, UserCache}

@Singleton
class LoginController @Inject()(implicit cache: CacheApi, val messagesApi: MessagesApi, user: UserImpl) extends Controller with I18nSupport {
    val prefix = "[LoginController]"

    def index = Action { implicit request =>
        val message = request.flash.get("message")
        Ok(views.html.login.index(cache, "", LoginForm.form, RegisterForm.form, message))
    }

    def login = Action { implicit request =>
        val f = LoginForm.form.bindFromRequest

        val user_opt = user.login(f.get.name, Security.md5(f.get.password))
        user_opt match {
            case Some(u) => {
                Logger.info(s"$prefix Login success id: ${u.id}")
                val uuid = UserCache.set(u)
                Redirect(routes.HomeController.index()).withSession(Security.session_name -> uuid)
            }
            case None => {
                Logger.info(s"$prefix Login Failed")
                Redirect(routes.LoginController.index()).flashing("message" -> "Login failed")
            }
        }
    }

    def logout = Action { implicit request =>
        val user_uuid = Security.getSessionUUID(request)
        UserCache.remove(user_uuid)

        Redirect(routes.LoginController.index()).withSession(request.session - Security.session_name)
    }
}
