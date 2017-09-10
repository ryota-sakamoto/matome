package controllers

import javax.inject._

import models.{AuthImpl, UserImpl}
import play.api.Logger
import play.api.mvc._
import play.cache.CacheApi
import utils.Security

@Singleton
class AuthController @Inject()(cache: CacheApi, authImpl: AuthImpl, userImpl: UserImpl) extends Controller {
    val prefix = "[AuthController]"

    def auth = Action { implicit request =>
        val key_opt = request.getQueryString("key")
        key_opt match {
            case Some(key) => {
                val encrypt_key = Security.encrypt(key)
                val auth_opt = authImpl.find(encrypt_key)

                auth_opt match {
                    case Some(a) => {
                        val count = userImpl.updateStatus(a.user_id, userImpl.certified)

                        if (count == 1) {
                            Ok("Success certified")
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