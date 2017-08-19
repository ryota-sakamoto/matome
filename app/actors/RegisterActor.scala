package actors

import akka.actor.Actor
import forms.Register
import play.Logger
import models.User
import utils.Security

class RegisterActor extends Actor {
    def receive = {
        case data: Register => {
            val email = data.email
            val name = data.name
            val password = Security.md5(data.password)

            val id = User.create(email, name, password, User.not_formal)
            id match {
                case Some(x) => Logger.info("create user id: %d".format(x))
                case None => Logger.error("create user failed")
            }
        }
    }
}