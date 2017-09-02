package utils

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import models.Blog
import models.aggregation._
import play.api.Logger

object Aggregation {
    val prefix = "[Aggregation]"

    val en_format = """\w{3}, \d{2} \w{3} \d{4} \d{2}:\d{2}:\d{2} \+\d{4}""".r
    val jp_format = """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+09:00""".r

    def getRSSUrl(blog: Blog): String = {
        blog.blog_type match {
            case Livedoor.blog_type => Livedoor.getRssURL(blog.url)
            case Hatena.blog_type => Hatena.getRssURL(blog.url)
            case Qiita.blog_type => Qiita.getRssURL(blog.url)
            case Ameblo.blog_type => Ameblo.getRssURL(blog.url)
            case _ => {
                Logger.error("Not Found Blog Type")
                ""
            }
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