package actors

import javax.inject.Inject

import akka.actor.Actor
import models.{Article, Blog}
import play.Logger
import play.api.libs.ws._
import utils.{Aggregation, DateUtil}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class AggregationActor @Inject()(ws_client: WSClient) extends Actor {
    def receive = {
        case blog: Blog => {
            implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
            val url = Aggregation.getRSSUrl(blog.url)
            val ws = ws_client.url(url)
            Logger.debug("receive aggregation: %s".format(url))

            val response = ws.get().map { r =>
                r.xml \ "_"
            }
            Await.ready(response, Duration.Inf)
            response.value.get match {
                case Success(s) => {
                    var last_update_date = blog.update_date
                    var name = blog.name
                    s.foreach { _s =>
                        _s.label match {
                            case "channel" => {
                                name = (_s \ "title").text
                            }
                            case "item" => {
                                val title =  (_s \ "title").text.filter{ c =>
                                    Character.isHighSurrogate(c) == Character.isLowSurrogate(c)
                                }
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
                            case _ =>
                        }
                    }
                    Blog.update(Blog(blog.id, name, blog.url, last_update_date))
                }
                case Failure(e) => {
                    printf("%s\n", e)
                }
            }
        }
    }
}