package forms

import play.api.data._
import play.api.data.Forms._

case class Login(name: String, password: String)

object LoginForm {
    val form = Form[Login](
        mapping(
            "name" -> nonEmptyText,
            "password" -> nonEmptyText
        )(Login.apply)(Login.unapply)
    )
}