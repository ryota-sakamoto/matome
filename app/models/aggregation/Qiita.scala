package models.aggregation
import play.api.libs.ws.WSClient
import utils.Aggregation

object Qiita extends AggregationTrait {
    val blog_type = "qiita"

    def aggregate(url: String)(implicit ws_client: WSClient): Set[ArticleData] = {
        val data = get(getRssURL(url))
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data match {
            case Some(s) => {
                s.foreach { _s =>
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
            }
            case None =>
        }
        set
    }

    def getRssURL(url: String) = {
        url + "/feed.atom"
    }
}