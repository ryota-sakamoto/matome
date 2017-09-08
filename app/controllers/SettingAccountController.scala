package controllers

import javax.inject.{Inject, Singleton}

import actions.AuthAction
import forms.SettingAccountForm
import models.UserImpl
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.cache.CacheApi
import utils.{Security, UserCache}

@Singleton
class SettingAccountController @Inject()(implicit cache: CacheApi, val messagesApi: MessagesApi, user: UserImpl) extends Controller with I18nSupport {
    val prefix = "[SettingAccountController]"

    def index = AuthAction ( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val message = request.flash.get("message")
            val alert_type = request.flash.get("type")
            val user_data = UserCache.get(user_uuid).get

            Ok(views.html.settings.account(cache, user_uuid, SettingAccountForm.form, user_data, alert_type, message))
        }
    )

    def save = AuthAction ( cache,
        Action { implicit request =>
            val f = SettingAccountForm.form.bindFromRequest
            val user_uuid = Security.getSessionUUID(request)
            val cache_user_data = UserCache.get(user_uuid).get

            val user_opt = user.login(cache_user_data.name, Security.md5(f.get.password))

            user_opt match {
                case Some(u) => {
                    val name = f.get.name
                    val email = f.get.email

                    if (user.checkExists(name)) {
                        Logger.info(s"$prefix Duplicate Name: ${u.name} => $name")
                        Redirect(routes.SettingAccountController.index()).flashing("message" -> "Duplicate Name", "type" -> "danger")
                    } else {
                        val new_user_data = u.copy(name = name, email = email)
                        user.update(new_user_data) match {
                            case 1 => {
                                Logger.info(s"$prefix Update Success name: ${u.name} => ${new_user_data.name}")
                                UserCache.update(user_uuid, new_user_data)
                                Redirect(routes.SettingAccountController.index()).flashing("message" -> "Update Success", "type" -> "success")
                            }
                            case _ => {
                                Logger.error(s"$prefix Update Error name: ${u.name} => ${new_user_data.name}")
                                Redirect(routes.SettingAccountController.index()).flashing("message" -> "Update Error", "type" -> "danger")
                            }
                        }
                    }
                }
                case None => {
                    Logger.error(s"$prefix Incorrect Password id: ${cache_user_data.id}")
                    Redirect(routes.SettingAccountController.index()).flashing("message" -> "Incorrect Password", "type" -> "danger")
                }
            }
        }
    )
}