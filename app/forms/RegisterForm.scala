package forms

import play.api.data._
import play.api.data.Forms._

case class Register(email: String, name: String, password: String)

object RegisterForm {
    val form = Form[Register](
        mapping(
            "email" -> nonEmptyText,
            "name" -> nonEmptyText,
            "password" -> nonEmptyText
        )(Register.apply)(Register.unapply)
    )
}