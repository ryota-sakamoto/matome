package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Props}
import controllers.routes
import forms.Register
import play.Logger
import models.{AuthImpl, UserImpl}
import play.api.libs.mailer.MailerClient
import utils.Security

class RegisterActor @Inject()(mailerClient: MailerClient, user: UserImpl, auth: AuthImpl, @Named("mailActor") mailActor: ActorRef) extends Actor {
    def receive = {
        case data: Register => {
            val email = data.email
            val name = data.name
            val password = Security.encrypt(data.password)

            val id = user.create(email, name, password, user.uncertified)
            id match {
                case Some(x) => {
                    Logger.info("create user id: %d".format(x))

                    val auth_key = Security.generateKey()
                    auth.create(x.toInt, Security.encrypt(auth_key))
                    val title = "please auth"
                    val auth_url = s"http://localhost:9000${routes.AuthController.auth().url}?key=$auth_key"

                    val message =
                        s"""|please auth
                            |$auth_url
                        """.stripMargin

                    mailActor ! (title, email, message)
                }
                case None => Logger.error("create user failed")
            }
        }
    }
}