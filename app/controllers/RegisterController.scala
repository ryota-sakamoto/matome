package controllers

import javax.inject._

import akka.actor.ActorRef
import forms.{Register, RegisterForm}
import models.UserImpl
import play.api.cache.AsyncCacheApi
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import utils.{Security, UserCache}

@Singleton
class RegisterController @Inject()(implicit cache: AsyncCacheApi, mailerClient: MailerClient, @Named("registerActor") registerActor: ActorRef, user_impl: UserImpl, messagesAction: MessagesActionBuilder) extends InjectedController {
    def index = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val user_uuid = Security.getSessionUUID(request)
        val user = UserCache.get(user_uuid)
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
        Ok(views.html.register.index(user, form, message))
    }

    def register = Action { implicit request: Request[AnyContent] =>
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
                    Ok(views.html.register.success(None))
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
