package utils

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import models.Blog
import models.aggregation._

object Aggregation {
    val prefix = "[Aggregation]"

    val en_format = """\w{3}, \d{2} \w{3} \d{4} \d{2}:\d{2}:\d{2} \+\d{4}""".r
    val jp_format = """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+09:00""".r

    def getRSSUrl(blog: Blog): String = {
        blog.url + blog.blog_type match {
            case Livedoor.blog_type => "/index.rdf"
            case Hatena.blog_type => "/rss"
            case _ => ""
        }
    }

    def checkEmoticon(c: Char): Boolean = {
        Character.isHighSurrogate(c) == Character.isLowSurrogate(c)
    }

    def convertToDate(str_date: String): Date = {
        val format = str_date match {
            case en_format() => new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            case jp_format() => new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            case _ => new SimpleDateFormat()
        }

        format.parse(str_date)
    }
}