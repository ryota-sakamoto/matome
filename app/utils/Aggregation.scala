package utils

import java.text.SimpleDateFormat
import java.util.Date

object Aggregation {
    val lineblog = "lineblog.me"
    val hateblo = "hateblo.jp"

    def getRSSUrl(url: String): String = { // TODO
        url + (if (url.contains(lineblog)) {
            "/index.rdf"
        } else if (url.contains(hateblo)) {
            "/rss"
        })
    }

    def checkEmoticon(c: Char): Boolean = {
        Character.isHighSurrogate(c) == Character.isLowSurrogate(c)
    }

    def convertToDate(str_date: String): Date = {
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
        format.parse(str_date)
    }
}