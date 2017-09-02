package models.aggregation
import utils.Aggregation

import scala.xml.NodeSeq

object Ameblo extends AggregationTrait {
    val blog_type = "ameblo"

    def aggregate(data: NodeSeq): Set[ArticleData] = {
        var set = Set.empty[ArticleData]

        data.foreach { item =>
            item.label match {
                case "item" => {
                    val title = (item \ "title").text
                    val link = (item \ "link").text
                    val date = (item \ "date").text
                    val update_date = Aggregation.convertToDate(date)

                    set += ArticleData(title, link, update_date)
                }
                case _ =>
            }
        }
        set
    }

    def getRssURL(url: String) = {
        val url_regex = """https?://ameblo.jp/(.+)/.*?""".r

        url match {
            case url_regex(u) => {
                val ameblo_id = u.split('/')(0)
                "http://feedblog.ameba.jp/rss/ameblo/" + ameblo_id
            }
            case _ => url
        }
    }
}