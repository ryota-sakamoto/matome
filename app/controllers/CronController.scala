package controllers

import javax.inject._

import play.api.mvc._
import akka.actor._
import models.Blog
import actors.AggregationActor
import play.api.libs.ws.WSClient

class CronController @Inject()(ws: WSClient) extends Controller {

    // TODO fix
    def aggregateArticle = Action {
        val system = ActorSystem("system")
        val actor = system.actorOf(Props(classOf[AggregationActor], ws))

        val b = Blog.find()
        b.foreach { blog =>
            actor ! blog
        }

        Ok("")
    }
}