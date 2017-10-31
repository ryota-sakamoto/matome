package filters

import javax.inject.Inject

import akka.stream.Materializer
import controllers.routes
import play.api.cache.AsyncCacheApi
import play.api.mvc.Results.Redirect
import play.api.mvc._
import play.api.routing.Router
import utils.{Security, UserCache}

import scala.concurrent.{ExecutionContext, Future}

class AuthFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext, cache: AsyncCacheApi) extends Filter {
    val prefix = "[AuthFilter]"

    def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
        val request = Request(requestHeader, AnyContent())
        val handler_opt = request.attrs.get(Router.Attrs.HandlerDef)
        handler_opt match {
            case Some(handler) => {
                val modifiers = handler.modifiers

                val path = modifiers.contains("noauth")
                val uuid = Security.getSessionUUID(request)

                val user = UserCache.get(uuid)
                (user, path) match {
                    case (Some(_), _) | (None, true) => {
                        nextFilter(requestHeader)
                    }
                    case _ => {
                        Future(Redirect(routes.LoginController.index()).withSession(request.session - Security.session_name).flashing("message" -> "Login Required"))
                    }
                }
            }
            case None => Future(Redirect(routes.HomeController.index()))
        }
    }
}