package actions

import controllers.routes
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.mvc.{Action, AnyContent, Request, Result}
import play.api.mvc.Results.Redirect

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import utils.{Security, UserCache}

case class AuthAction[A](cache: AsyncCacheApi, action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Future[Result] = {
        val prefix = "[AuthAction]"
        Logger.info(s"$prefix start")
        val uuid = Security.getSessionUUID(request.asInstanceOf[Request[AnyContent]])
        val user = UserCache.get(uuid)(cache)

        user match {
            case Some(u) => {
                Logger.info(s"$prefix auth success")
                action(request)
            }
            case None => {
                Logger.info(s"$prefix auth failed")
                Future(Redirect(routes.LoginController.index()).withSession(request.session - Security.session_name).flashing("message" -> "Login Required"))
            }
        }
    }

    lazy val parser = action.parser

    override def executionContext: ExecutionContext = ???
}