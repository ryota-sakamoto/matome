package forms

import play.api.data._
import play.api.data.Forms._

case class BlogEdit(name: String, url: String, blog_type_id: Int)

object BlogEditForm {
    val form = Form[BlogEdit](
        mapping(
            "name" -> nonEmptyText,
            "url" -> nonEmptyText,
            "blog_type_id" -> number
        )(BlogEdit.apply)(BlogEdit.unapply
    ))
}