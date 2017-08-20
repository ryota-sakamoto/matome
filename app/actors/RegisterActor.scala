package actors

import javax.inject.Inject

import akka.actor.{Actor, Props}
import forms.Register
import play.Logger
import models.User
import play.api.libs.mailer.MailerClient
import utils.Security

class RegisterActor @Inject()(mailerClient: MailerClient) extends Actor {
    def receive = {
        case data: Register => {
            val email = data.email
            val name = data.name
            val password = Security.md5(data.password)

            val id = User.create(email, name, password, User.not_formal)
            id match {
                case Some(x) => {
                    Logger.info("create user id: %d".format(x))
                    val mail = this.context.actorOf(Props(classOf[MailActor], mailerClient))
                    mail ! ("Register Success",email, "Ok")
                }
                case None => Logger.error("create user failed")
            }
        }
    }
}