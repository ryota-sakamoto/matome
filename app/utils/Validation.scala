package utils

import play.api.mvc.{AnyContent, Request}

object Validation {
    private val number_only = """^(\d+)$"""
    private val url_regex = """https?://[a-zA-Z0-9]+?\.[a-zA-Z0-9/_:]+"""

    def validate(args: (String, Symbol)*)(implicit request: Request[AnyContent]): Boolean = {
        val m = for (
            arg <- args
        ) yield request.getQueryString(arg._1) match {
            case Some(s) => arg._2 match {
                case 'int => s.matches(number_only)
                case 'url => s.matches(url_regex)
                case _ => false
            }
            case None => false
        }

        !m.contains(false)
    }
}