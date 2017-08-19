package controllers

import javax.inject._

import actors.RegisterActor
import akka.actor.{ActorSystem, Props}
import forms.RegisterForm
import play.api.mvc._
import play.cache.CacheApi

@Singleton
class RegisterController @Inject()(cache: CacheApi)extends Controller {
    def register = Action { implicit request =>
        val f = RegisterForm.form.bindFromRequest

        val system = ActorSystem("system")
        val actor = system.actorOf(Props[RegisterActor])

        actor ! f.get

        Ok("Send Create User Request")
    }
}
