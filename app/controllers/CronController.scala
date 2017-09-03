package controllers

import javax.inject._

import play.api.mvc._
import akka.actor._
import models.BlogImpl
import play.api.libs.mailer.MailerClient
import play.api.libs.ws.WSClient

class CronController @Inject()(system: ActorSystem, ws: WSClient, mailerClient: MailerClient, blog: BlogImpl, @Named("aggregationActor") aggregationActor: ActorRef) extends Controller {

    def aggregateArticle = Action {
        val b = blog.find()
        aggregationActor ! b

        Ok("")
    }
}