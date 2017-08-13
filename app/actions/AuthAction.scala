package actions

import javax.inject.Inject

import controllers.routes
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Request, Result}
import play.api.mvc.Results.Redirect
import play.cache.CacheApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.{Security, UserCache}

case class AuthAction[A] @Inject() (cache: CacheApi, action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Future[Result] = {
        val prefix = "[AuthAction]"
        Logger.info(s"$prefix start")
        val uuid = Security.getSessionUUID(request.asInstanceOf[Request[AnyContent]])
        val user = UserCache.get(cache, uuid)

        user match {
            case Some(u) => {
                Logger.info(s"$prefix auth success")
                action(request)
            }
            case None => {
                Logger.info(s"$prefix auth failed")
                Future(Redirect(routes.LoginController.index()))
            }
        }
    }

    lazy val parser = action.parser
}