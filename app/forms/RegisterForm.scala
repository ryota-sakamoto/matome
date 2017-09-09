package forms

import play.api.data._
import play.api.data.Forms._

case class Register(email: String, name: String, password: String, confirm_password: String)

object RegisterForm {
    val form = Form[Register](
        mapping(
            "email" -> nonEmptyText,
            "name" -> nonEmptyText,
            "password" -> nonEmptyText,
            "confirm_password" -> nonEmptyText
        )(Register.apply)(Register.unapply).verifying("Incorrect of Confirm Password", f => f.password == f.confirm_password)
    )
}