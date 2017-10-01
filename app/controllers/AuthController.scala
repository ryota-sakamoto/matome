package controllers

import javax.inject._

import models.{AuthImpl, UserImpl}
import play.api.Logger
import play.api.mvc._
import play.api.cache.AsyncCacheApi
import utils.Security

@Singleton
class AuthController @Inject()(cache: AsyncCacheApi, authImpl: AuthImpl, userImpl: UserImpl) extends InjectedController {
    val prefix = "[AuthController]"

    def auth = Action { implicit request: Request[AnyContent] =>
        val key_opt = request.getQueryString("key")
        key_opt match {
            case Some(key) => {
                val encrypt_key = Security.encrypt(key)
                val auth_opt = authImpl.find(encrypt_key)

                auth_opt match {
                    case Some(a) => {
                        val count = userImpl.updateStatus(a.user_id, userImpl.certified)

                        if (count == 1) {
                            Logger.info(s"$prefix Success certified user_id: ${a.user_id}")
                            Redirect(routes.LoginController.index()).flashing("message" -> "auth success", "message_type" -> "success")
                        } else {
                            Logger.error(s"$prefix Status Update error ${a.user_id}")
                            BadRequest(views.html.template.notfound("Bad Request"))
                        }
                    }
                    case None => {
                        Logger.error(s"$prefix user is not found")
                        BadRequest(views.html.template.notfound("Bad Request"))
                    }
                }
            }
            case None => {
                Logger.error(s"$prefix key is not found")
                BadRequest(views.html.template.notfound("Bad Request"))
            }
        }
    }
}