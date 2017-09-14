package models.aggregation
import play.api.libs.ws.WSClient
import utils.Aggregation

object Ameblo extends AggregationTrait {
    val blog_type = "ameblo"

    def aggregate(url: String)(implicit ws_client: WSClient): Set[ArticleData] = {
        val data = get(getRssURL(url))
        var set: Set[ArticleData] = Set.empty[ArticleData]

        data match {
            case Some(s) => {
                s.foreach { item =>
                    item.label match {
                        case "item" => {
                            val title = convert((item \ "title").text)
                            val link = convert((item \ "link").text)
                            val date = convert((item \ "date").text)
                            val update_date = Aggregation.convertToDate(date)

                            set += ArticleData(title, link, update_date)
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
        val url_regex = """https?://ameblo.jp/(.+)/.*?""".r

        url match {
            case url_regex(u) => {
                val ameblo_id = u.split('/')(0)
                "http://feedblog.ameba.jp/rss/ameblo/" + ameblo_id
            }
            case _ => url
        }
    }

    def convert(c: String): String = {
        new String(c.getBytes("ISO8859_1"), "UTF-8")
    }
}