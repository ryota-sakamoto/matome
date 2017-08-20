package actors

import actors.status.End
import play.api.libs.mailer._
import akka.actor.Actor
import play.api.Logger

class MailActor(mailerClient: MailerClient) extends Actor {
    val prefix = "[MailActor]"
    def receive = {
        case data: (String, String, String) => {
            val email = Email(
                subject = data._1,
                from = "from@ryota.sakamoto",
                to = Seq(data._2),
                bodyText = Option(data._3)
            )
            mailerClient.send(email)
            Logger.info(s"$prefix ${email.from} => ${email.to.mkString(",")}, subject: ${email.subject}, body: ${email.bodyText}")
            context.parent ! End()
        }
    }
}