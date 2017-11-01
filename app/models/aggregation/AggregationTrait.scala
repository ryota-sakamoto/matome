package models.aggregation

import java.util.Date

import play.api.Logger
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.xml.NodeSeq

case class ArticleData(title: String, link: String, update_date: Date)

trait AggregationTrait {
    val prefix = "[AggregationTrait]"
    val blog_type: String

    def aggregate(url: String)(implicit ws_client: WSClient): Seq[ArticleData]

    def get(url: String)(implicit ws_client: WSClient): Option[NodeSeq] = {
        implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
        val ws = ws_client.url(url)
        Logger.debug(s"$prefix receive aggregation: $url")

        val response = ws.get().map { r =>
            r.xml \ "_"
        }
        Await.ready(response, Duration.Inf)
        response.value.get match {
            case Success(s: NodeSeq) => Some(s)
            case Failure(e) => {
                Logger.error(s"$prefix $e")
                None
            }
        }
    }
}