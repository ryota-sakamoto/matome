package actors

import javax.inject.Inject

import akka.actor.Actor
import models.aggregation.{Hatena, Livedoor}
import models.{Article, Blog}
import play.Logger
import play.api.libs.ws._
import utils.Aggregation

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class AggregationActor @Inject()(ws_client: WSClient) extends Actor {
    val prefix = "[AggregationActor]"
    def receive = {
        case blog: Blog => {
            implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
            val url = Aggregation.getRSSUrl(blog.url)
            val ws = ws_client.url(url)
            Logger.debug(s"$prefix receive aggregation: $url")

            val response = ws.get().map { r =>
                r.xml \ "_"
            }
            Await.ready(response, Duration.Inf)
            response.value.get match {
                case Success(s) => {
                    val article_data = "hatena" match { // TODO
                        case Livedoor.blog_type => Livedoor.aggregate(s)
                        case Hatena.blog_type => Hatena.aggregate(s)
                        case _ => {
                            Logger.error("Not Found Blog Type")
                            Seq.empty
                        }
                    }

                    var name = blog.name
                    var last_update_date = blog.update_date

                    article_data.foreach { data =>
                        if (data.update_date.compareTo(blog.update_date) > 0) {
                            Article.insert(Article(0, blog.id, data.title, data.link, data.update_date))
                            if (data.update_date.compareTo(last_update_date) > 0) {
                                last_update_date = data.update_date
                            }
                        }
                    }

                    Blog.update(Blog(blog.id, name, blog.url, last_update_date))
                }
                case Failure(e) => {
                    Logger.error(s"$prefix $e")
                }
            }
        }
    }
}