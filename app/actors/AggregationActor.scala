package actors

import javax.inject.Inject

import actors.status.End
import akka.actor.{Actor, PoisonPill, Props}
import models.aggregation.{Ameblo, Hatena, Livedoor, Qiita}
import models.elasticsearch.Elastic
import models.{Article, Blog, BlogImpl}
import play.Logger
import play.api.libs.mailer.MailerClient
import play.api.libs.ws._
import utils.Aggregation

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class AggregationActor @Inject()(ws_client: WSClient, mailerClient: MailerClient, blog: BlogImpl) extends Actor {
    val prefix = "[AggregationActor]"
    var blog_count = 0

    def receive = {
        case blog_list: Seq[Blog] => {
            blog_count = blog_list.length
            blog_list.foreach { blog_data =>
                implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
                val url = Aggregation.getRSSUrl(blog_data)
                val ws = ws_client.url(url)
                Logger.debug(s"$prefix receive aggregation: $url")

                val response = ws.get().map { r =>
                    r.xml \ "_"
                }
                Await.ready(response, Duration.Inf)
                response.value.get match {
                    case Success(s) => {
                        val article_data = blog_data.blog_type match {
                            case Livedoor.blog_type => Livedoor.aggregate(s)
                            case Hatena.blog_type => Hatena.aggregate(s)
                            case Qiita.blog_type => Qiita.aggregate(s)
                            case Ameblo.blog_type => Ameblo.aggregate(s)
                            case _ => {
                                Logger.error("Not Found Blog Type")
                                Seq.empty
                            }
                        }

                        var last_update_date = blog_data.update_date

                        article_data.foreach { data =>
                            if (data.update_date.compareTo(blog_data.update_date) > 0) {
                                val article = Article(0, blog_data.id, data.title, data.link, data.update_date)
                                Article.insert(article)
                                Elastic.insert(blog_data.name, article)
                                if (data.update_date.compareTo(last_update_date) > 0) {
                                    last_update_date = data.update_date
                                }
                            }
                        }

                        if (last_update_date != blog_data.update_date) {
                            blog.update(blog_data.id, last_update_date)

                            if (blog_data.notification) {
                                val mail = this.context.actorOf(Props(classOf[MailActor], mailerClient))
                                mail ! ("Blog update", blog_data.user_email, blog_data.name)
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