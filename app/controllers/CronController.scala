package controllers

import javax.inject._

import play.api.mvc._
import akka.actor._
import models.Blog
import actors.AggregationActor
import play.api.libs.mailer.MailerClient
import play.api.libs.ws.WSClient

class CronController @Inject()(system: ActorSystem, ws: WSClient, mailerClient: MailerClient) extends Controller {

    // TODO fix
    def aggregateArticle = Action {
        val actor = system.actorOf(Props(classOf[AggregationActor], ws, mailerClient))

        val b = Blog.find()
        actor ! b

        Ok("")
    }
}