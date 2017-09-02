package models.aggregation

import java.util.Date

import scala.xml.NodeSeq

case class ArticleData(title: String, link: String, update_date: Date)

trait AggregationTrait {
    val blog_type: String

    def aggregate(data: NodeSeq): Set[ArticleData]

    def getRssURL(url: String): String
}