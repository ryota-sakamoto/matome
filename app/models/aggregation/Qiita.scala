package models.aggregation
import utils.Aggregation

import scala.xml.NodeSeq

object Qiita extends AggregationTrait {
    val blog_type = "qiita"

    def aggregate(data: NodeSeq): Set[ArticleData] = {
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data.foreach { _s =>
            _s.label match {
                case "entry" => {
                    val title =  (_s \ "title").text.filter{ Aggregation.checkEmoticon }
                    val link = (_s \ "url").text
                    val date = (_s \ "published").text
                    val update_date = Aggregation.convertToDate(date)

                    set = set + ArticleData(title, link, update_date)
                }
                case _ =>
            }
        }
        set
    }

    def getRssURL(url: String) = {
        url + "/feed.atom"
    }
}