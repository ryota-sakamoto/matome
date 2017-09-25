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
        val message_type = request.flash.get("message_type").getOrElse("danger")
        Ok(views.html.login.index(cache, "", LoginForm.form, RegisterForm.form, message, message_type))
    }

    def login = Action { implicit request =>
        val f = LoginForm.form.bindFromRequest

        f.value match {
            case Some(form) => {
                val user_opt = user.login(form.name, Security.encrypt(form.password))
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
            case None => BadRequest(views.html.template.notfound("Bad Request"))
        }
    }

    def logout = Action { implicit request =>
        val user_uuid = Security.getSessionUUID(request)
        UserCache.remove(user_uuid)

        Redirect(routes.LoginController.index()).withSession(request.session - Security.session_name)
    }
}
