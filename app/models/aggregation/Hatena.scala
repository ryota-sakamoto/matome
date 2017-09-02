package models.aggregation

import utils.Aggregation
import scala.xml.NodeSeq

object Hatena extends AggregationTrait {
    val blog_type = "hatena"

    def aggregate(data: NodeSeq): Set[ArticleData] = {
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data.foreach { _s =>
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
        set
    }

    def getRssURL(url: String) = {
        url + "/rss"
    }
}