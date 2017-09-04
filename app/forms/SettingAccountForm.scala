package forms

import play.api.data._
import play.api.data.Forms._

case class SettingAccount(name: String, email: String, password: String)

object SettingAccountForm {
    val form = Form[SettingAccount](
        mapping(
            "name" -> nonEmptyText,
            "email" -> nonEmptyText,
            "password" -> nonEmptyText
        )(SettingAccount.apply)(SettingAccount.unapply)
    )
}