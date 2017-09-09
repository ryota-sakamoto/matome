package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Props}
import forms.Register
import play.Logger
import models.UserImpl
import play.api.libs.mailer.MailerClient
import utils.Security

class RegisterActor @Inject()(mailerClient: MailerClient, user: UserImpl, @Named("mailActor") mailActor: ActorRef) extends Actor {
    def receive = {
        case data: Register => {
            val email = data.email
            val name = data.name
            val password = Security.encrypt(data.password)

            val id = user.create(email, name, password, user.not_formal)
            id match {
                case Some(x) => {
                    Logger.info("create user id: %d".format(x))
                    mailActor ! ("Register Success",email, "Ok")
                }
                case None => Logger.error("create user failed")
            }
        }
    }
}