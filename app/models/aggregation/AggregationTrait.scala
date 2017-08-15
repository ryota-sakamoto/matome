package models.aggregation

import scala.xml.NodeSeq

trait AggregationTrait[T] {
    val blog_type: String

    def aggregate(data: NodeSeq): Set[T]
}