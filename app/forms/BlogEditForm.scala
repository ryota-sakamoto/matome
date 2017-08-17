package forms

import play.api.data._
import play.api.data.Forms._

case class BlogEdit(name: String, url: String)

object BlogEditForm {
    val form = Form[BlogEdit](
        mapping(
            "name" -> nonEmptyText,
            "url" -> nonEmptyText
        )(BlogEdit.apply)(BlogEdit.unapply
    ))
}