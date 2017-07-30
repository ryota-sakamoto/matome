package utils

object Aggregation {
    def getRSSUrl(url: String): String = {
        url match {
            case _ => url + "/index.rdf"
        }
    }
}