package actors

import actors.status.End
import play.api.libs.mailer._
import akka.actor.Actor
import play.api.Logger

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

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
            val send_mail = Future {
                mailerClient.send(email)
            }

            Await.ready(send_mail, Duration.Inf)
            send_mail.value.get match {
                case Success(s) => Logger.info(s"$prefix ${email.from} => ${email.to.mkString(",")}, subject: ${email.subject}, body: ${email.bodyText}")
                case Failure(e) => Logger.error(s"$prefix $e")
            }
            context.parent ! End()
        }
    }
}