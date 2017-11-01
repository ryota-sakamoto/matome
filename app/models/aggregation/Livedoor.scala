package models.aggregation

import play.api.libs.ws.WSClient
import utils.Aggregation

object Livedoor extends AggregationTrait {
    val blog_type = "livedoor"

    def aggregate(url: String)(implicit ws_client: WSClient): Seq[ArticleData] = {
        val data = get(getRssURL(url))
        var set: Seq[ArticleData] = Seq.empty[ArticleData]

        data match {
            case Some(s) => {
                s.foreach { _s =>
                    _s.label match {
                        case "channel" => {
                        }
                        case "item" => {
                            val title =  (_s \ "title").text.filter{ Aggregation.checkEmoticon }
                            val link = (_s \ "link").text
                            val date = (_s \ "date").text
                            val update_date = Aggregation.convertToDate(date)

                            set = set :+ ArticleData(title, link, update_date)
                        }
                        case _ =>
                    }
                }
            }
            case None =>
        }
        set
    }

    def getRssURL(url: String) = {
        url + "/index.rdf"
    }
}