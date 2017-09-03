package actors

import javax.inject.Inject

import actors.status.End
import akka.actor.{Actor, PoisonPill, Props}
import models.aggregation.{Ameblo, Hatena, Livedoor, Qiita}
import models.elasticsearch.Elastic
import models.{Article, Blog, BlogImpl, BlogTypeImpl, UserImpl}
import play.Logger
import play.api.libs.mailer.MailerClient
import play.api.libs.ws._

class AggregationActor @Inject()(implicit ws_client: WSClient, mailerClient: MailerClient, blog: BlogImpl, blog_type_impl: BlogTypeImpl, user: UserImpl) extends Actor {
    val prefix = "[AggregationActor]"
    var blog_count = 0

    def receive = {
        case blog_list: Seq[Blog] => {
            blog_count = blog_list.length
            blog_list.foreach { blog_data =>
                val blog_type = blog_type_impl.findById(blog_data.blog_type_id) match {
                    case Some(x) => x.name
                    case None => ""
                }
                val article_data = blog_type match {
                    case Livedoor.blog_type => Livedoor.aggregate(blog_data.url)
                    case Hatena.blog_type => Hatena.aggregate(blog_data.url)
                    case Qiita.blog_type => Qiita.aggregate(blog_data.url)
                    case Ameblo.blog_type => Ameblo.aggregate(blog_data.url)
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

                    val email = user.findById(blog_data.user_id) match {
                        case Some(x) => x.email
                        case None => ""
                    }

                    if (blog_data.notification) {
                        val mail = this.context.actorOf(Props(classOf[MailActor], mailerClient))
                        mail ! ("Blog update", email, blog_data.name)
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