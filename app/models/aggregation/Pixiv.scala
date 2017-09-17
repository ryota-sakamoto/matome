package models.aggregation

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import play.api.libs.ws.WSClient
import utils.Aggregation

object Pixiv extends AggregationTrait {
    val blog_type: String = "pixiv"
    val date_regex = """https?://.*img-master/img/(\d+)/(\d+)/(\d+)/(\d+)/(\d+)/(\d+)/.+""".r

    def aggregate(url: String)(implicit ws_client: WSClient): Set[ArticleData] = {
        var set = Set.empty[ArticleData]

        val document = Jsoup.connect("https://www.pixiv.net/search.php").data("word", "東方").get()
        val images = document.getElementsByClass("image-item")

        for (i <- 0 until images.size()) {
            val image = images.get(i)

            val upload_date = image.getElementsByTag("img").first().attr("data-src") match {
                case date_regex(y, m, d, h, _i, s) => "%s-%s-%sT%s:%s:%s.000Z".format(y, m, d, h, _i, s)
            }
            val update_date = Aggregation.convertToDate(upload_date)

            val a_tag_opt = getElementByTagByIndex(image, "a", 1)

            val data = for {
                a_tag <- a_tag_opt
                pixiv_url <- getAttr(a_tag, "href")
                h1_tags <- getElementsByTag(a_tag, "h1")
                title <- getAttr(h1_tags.get(0), "title")
            } yield {
                ArticleData(title, s"https://www.pixiv.net$pixiv_url", update_date)
            }

            data match {
                case Some(article) => set = set + article
                case None =>
            }
        }

        set
    }

    def getAttr(target: Element, attrName: String): Option[String] = {
        Option(target.attr(attrName))
    }

    def getElementsByTag(target: Element, tag: String): Option[Elements] = {
        val tags = target.getElementsByTag(tag)

        if (tags.size() == 0) {
            None
        } else {
            Some(tags)
        }
    }

    def getElementByTagByIndex(target: Element, tag: String, index: Int): Option[Element] = {
        Option(target.getElementsByTag(tag).get(index))
    }
}