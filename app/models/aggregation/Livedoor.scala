package models.aggregation

import utils.Aggregation
import scala.xml.NodeSeq

object Livedoor extends AggregationTrait {
    val blog_type = "livedoor"

    def aggregate(data: NodeSeq): Set[ArticleData] = {
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data.foreach { _s =>
            _s.label match {
                case "channel" => {
//                    name = (_s \ "title").text
                }
                case "item" => {
                    val title =  (_s \ "title").text.filter{ Aggregation.checkEmoticon }
                    val link = (_s \ "link").text
                    val date = (_s \ "date").text
                    val update_date = Aggregation.convertToDate(date)

                    set = set + ArticleData(title, link, update_date)
                }
                case _ =>
            }
        }
        set
    }

    def getRssURL(url: String) = {
        url + "/index.rdf"
    }
}