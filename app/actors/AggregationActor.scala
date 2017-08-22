package actors

import javax.inject.Inject

import actors.status.End
import akka.actor.{Actor, PoisonPill, Props}
import models.aggregation.{Hatena, Livedoor}
import models.{Article, Blog}
import play.Logger
import play.api.libs.mailer.MailerClient
import play.api.libs.ws._
import utils.Aggregation

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class AggregationActor @Inject()(ws_client: WSClient, mailerClient: MailerClient) extends Actor {
    val prefix = "[AggregationActor]"
    var blog_count = 0

    def receive = {
        case blog_list: Seq[Blog] => {
            blog_count = blog_list.length
            blog_list.foreach { blog =>
                implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
                val url = Aggregation.getRSSUrl(blog)
                val ws = ws_client.url(url)
                Logger.debug(s"$prefix receive aggregation: $url")

                val response = ws.get().map { r =>
                    r.xml \ "_"
                }
                Await.ready(response, Duration.Inf)
                response.value.get match {
                    case Success(s) => {
                        val article_data = blog.blog_type match {
                            case Livedoor.blog_type => Livedoor.aggregate(s)
                            case Hatena.blog_type => Hatena.aggregate(s)
                            case _ => {
                                Logger.error("Not Found Blog Type")
                                Seq.empty
                            }
                        }

                        var last_update_date = blog.update_date

                        article_data.foreach { data =>
                            if (data.update_date.compareTo(blog.update_date) > 0) {
                                Article.insert(Article(0, blog.id, data.title, data.link, data.update_date))
                                if (data.update_date.compareTo(last_update_date) > 0) {
                                    last_update_date = data.update_date
                                }
                            }
                        }

                        if (last_update_date != blog.update_date) {
                            Blog.update(blog.id, last_update_date)

                            if (blog.notification) {
                                val mail = this.context.actorOf(Props(classOf[MailActor], mailerClient))
                                mail ! ("Blog update", blog.user_email, blog.name)
                            }
                        }
                    }
                    case Failure(e) => {
                        Logger.error(s"$prefix $e")
                    }
                }
            }
        }
        case End() => {
            if (blog_count == 0) {
                self ! PoisonPill
            } else {
                blog_count = blog_count - 1
            }
        }
    }
}