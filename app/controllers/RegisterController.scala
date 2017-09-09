package controllers

import javax.inject._

import akka.actor.ActorRef
import forms.{Register, RegisterForm}
import models.UserImpl
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class RegisterController @Inject()(cache: CacheApi, mailerClient: MailerClient, @Named("registerActor") registerActor: ActorRef, user_impl: UserImpl, val messagesApi: MessagesApi) extends Controller with I18nSupport {
    def index = Action { implicit request =>
        val flash = request.flash
        val message = flash.get("message")
        val form = RegisterForm.form.fill(
            Register(
                email = flash.get("email").getOrElse(""),
                name = flash.get("name").getOrElse(""),
                password = "",
                confirm_password = ""
            )
        )
        Ok(views.html.register.index(cache, "", form, message))
    }

    def register = Action { implicit request =>
        val f = RegisterForm.form.bindFromRequest

        val form_error = f.errors.map( error =>
            error.key match {
                case "name" => "Name Required"
                case "email" => "Email Required"
                case "password" => "Password Required"
                case "confirm_password" => "Confirm Password Required"
                case _ => error.message
            }
        )

        val name = f.data.getOrElse("name", "")
        val email = f.data.getOrElse("email", "")

        val name_error = if (user_impl.checkExists(name)) {
            Seq[String](s"Already Use Name [ $name ]")
        } else {
            Seq.empty[String]
        }

        val email_error = if (user_impl.checkExistsByEmail(email)) {
            Seq[String](s"Already Use Email [ $email ]")
        } else {
            Seq.empty[String]
        }

        val error = form_error ++ name_error ++ email_error

        if (error.isEmpty) {
            f.value match {
                case Some(form) => {
                    registerActor ! form
                    Ok("Send Create User Request")
                }
                case None => {
                    BadRequest(views.html.template.notfound("Bad Request"))
                }
            }
        } else {
            Redirect(routes.RegisterController.index()).flashing(
                "name" -> name,
                "email" -> email,
                "message" -> error.mkString(",")
            )
        }
    }
}
