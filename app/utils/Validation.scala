package utils

object Validation {
    private val number_only = """^(\d+)$""".r
    private val url_regex = """https?://[a-zA-Z0-9]+?\.[a-zA-Z0-9/_:]+""".r

    def isNumber(t: Option[String]): Boolean = {
        t match {
            case Some(x) => {
                x match {
                    case number_only(n) => true
                    case _ => false
                }
            }
            case None => false
        }
    }

    def isURL(url: String): Boolean = {
        url match {
            case url_regex() => true
            case _ => false
        }
    }
}