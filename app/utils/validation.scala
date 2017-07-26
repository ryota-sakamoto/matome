package utils

object validation {
    private val number_only = """^(\d+)$""".r

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
}