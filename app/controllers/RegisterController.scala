package controllers

import javax.inject._

import akka.actor.ActorRef
import forms.RegisterForm
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class RegisterController @Inject()(cache: CacheApi, mailerClient: MailerClient, @Named("registerActor") registerActor: ActorRef) extends Controller {
    def register = Action { implicit request =>
        val f = RegisterForm.form.bindFromRequest

        f.value match {
            case Some(form) => {
                registerActor ! form
                Ok("Send Create User Request")
            }
            case None => {
                BadRequest(views.html.template.notfound("Bad Request"))
            }
        }
    }
}
