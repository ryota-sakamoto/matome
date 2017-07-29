package controllers

import javax.inject._

import models.{Article, Blog}
import play.api.mvc._
import play.api.libs.ws._
import utils.{Aggregation, DateUtil}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await}
import scala.util.{Failure, Success}

class CronController @Inject()(ws: WSClient) extends Controller {

    // TODO fix
    def aggregateArticle = Action {
        implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
        val b = Blog.find()
        b.foreach { blog =>
            val url = Aggregation.getRSSUrl(blog.url)
            val response = ws.url(url).get().map { r =>
                r.xml \ "item"
            }
            Await.ready(response, Duration.Inf)
            response.value.get match {
                case Success(s) => {
                    s.foreach { _s =>
                        val title =  (_s \ "title").text
                        val link = (_s \ "link").text
                        val date = (_s \ "date").text
                        Article.insert(Article(0, 1, title, link, DateUtil.convertToDate(date)))
                    }
                }
                case Failure(e) => printf("%s\n", e)
            }
        }
        Ok("")
    }
}