package actors

import akka.actor.Actor
import models.{Article, Blog}
import play.Logger
import play.api.libs.ws._
import play.api.Play.current
import utils.{Aggregation, DateUtil}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class AggregationActor extends Actor {
    def receive = {
        case blog: Blog => {
            implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
            val url = Aggregation.getRSSUrl(blog.url)
            val ws = WS.url(url)
            Logger.debug("receive aggregation: %s".format(url))

            val response = ws.get().map { r =>
                r.xml \ "item"
            }
            Await.ready(response, Duration.Inf)
            response.value.get match {
                case Success(s) => {
                    var last_update_date = blog.update_date
                    s.foreach { _s =>
                        val title =  (_s \ "title").text
                        val link = (_s \ "link").text
                        val date = (_s \ "date").text
                        val update_date = DateUtil.convertToDate(date)
                        if (update_date.compareTo(blog.update_date) > 0) {
                            Article.insert(Article(0, blog.id, title, link, update_date))
                            if (update_date.compareTo(last_update_date) > 0) {
                                last_update_date = update_date
                            }
                        }
                    }
                    Blog.update(Blog(blog.id, blog.name, blog.url, last_update_date))
                }
                case Failure(e) => {
                    printf("%s\n", e)
                }
            }
        }
    }
}