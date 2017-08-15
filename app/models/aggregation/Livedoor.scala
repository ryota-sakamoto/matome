package models.aggregation

import java.util.Date

import utils.Aggregation
import scala.xml.NodeSeq

case class Livedoor(title: String, link: String, update_date: Date)

object Livedoor extends AggregationTrait[Livedoor] {
    val blog_type = "livedoor"

    def aggregate(data: NodeSeq): Set[Livedoor] = {
        var set: Set[Livedoor] = Set.empty[Livedoor]

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

                    set = set + Livedoor(title, link, update_date)
                }
                case _ =>
            }
        }
        set
    }
}