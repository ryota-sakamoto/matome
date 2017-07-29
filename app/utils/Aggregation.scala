package utils

object Aggregation {
    def getRSSUrl(url: String): String = {
        url + "/index.rdf"
    }
}