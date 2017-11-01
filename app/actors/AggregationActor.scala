package actors

import javax.inject.Inject

import actors.status.End
import akka.actor.{Actor, PoisonPill, Props}
import models.aggregation._
import models.elasticsearch.ElasticSearch
import models._
import play.Logger
import play.api.libs.mailer.MailerClient
import play.api.libs.ws._
import utils.Security

class AggregationActor @Inject()(implicit ws_client: WSClient, mailerClient: MailerClient, blog: BlogImpl, blog_type_impl: BlogTypeImpl, user: UserImpl, article: ArticleImpl, elasticSearch: ElasticSearch) extends Actor {
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
                    case Pixiv.blog_type => Pixiv.aggregate(blog_data.url)
                    case _ => {
                        Logger.error("Not Found Blog Type")
                        Seq.empty
                    }
                }

                val new_articles = article_data filter { data =>
                    data.update_date.compareTo(blog_data.update_date) > 0
                } map { data =>
                    val id = Security.generateUUID()
                    val article_data = Article(id, blog_data.id, data.title, data.link, data.update_date)
                    article_data
                }

                if (new_articles.nonEmpty) {
                    article.bulkInsert(new_articles)
                    elasticSearch.index(blog_data.name, new_articles)

                    val blog_update_date = new_articles map {
                        _.update_date
                    } reduce { (a, b) =>
                        if (a.compareTo(b) > 0) {
                            a
                        } else {
                            b
                        }
                    }

                    if (blog_update_date.compareTo(blog_data.update_date) > 0) {
                        blog.update(blog_data.id, blog_update_date)

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