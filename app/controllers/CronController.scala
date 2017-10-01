package controllers

import javax.inject._

import play.api.mvc._
import akka.actor._
import models.BlogImpl

class CronController @Inject()(blog: BlogImpl, @Named("aggregationActor") aggregationActor: ActorRef) extends InjectedController {

    def aggregateArticle = Action {
        val b = blog.findAll()
        aggregationActor ! b

        Ok("")
    }
}