package controllers

import javax.inject._
import play.api.mvc._
import akka.actor._

import models.Blog
import actors.AggregationActor

class CronController @Inject() extends Controller {

    // TODO fix
    def aggregateArticle = Action {
        val system = ActorSystem("system")
        val actor = system.actorOf(Props[AggregationActor])

        val b = Blog.find()
        b.foreach { blog =>
            actor ! blog
        }

        Ok("")
    }
}