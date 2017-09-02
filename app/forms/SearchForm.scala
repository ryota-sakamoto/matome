package forms

import play.api.data._
import play.api.data.Forms._

case class Search(word: String)

object SearchForm {
    val form = Form[Search](
        mapping(
            "word" -> nonEmptyText
        )(Search.apply)(Search.unapply)
    )
}