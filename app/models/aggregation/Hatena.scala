package models.aggregation

import play.api.libs.ws.WSClient
import utils.Aggregation

object Hatena extends AggregationTrait {
    val blog_type = "hatena"

    def aggregate(url: String)(implicit ws_client: WSClient): Set[ArticleData] = {
        val data = get(getRssURL(url))
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data match {
            case Some(s) => {
                s.foreach { _s =>
                    _s.label match {
                        case "channel" => {
                            val item = _s \ "item"
                            item.foreach { __s =>
                                val title =  (__s \ "title").text.filter{ Aggregation.checkEmoticon }
                                val link = (__s \ "link").text
                                val date = (__s \ "pubDate").text
                                val update_date = Aggregation.convertToDate(date)

                                set = set + ArticleData(title, link, update_date)
                            }
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
        url + "/rss"
    }
}