package route

import javax.inject.Inject

import controllers.ApiController
import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router.Routes
import play.api.routing.{Router, SimpleRouter}

import scala.runtime.AbstractPartialFunction

class ApiRouter @Inject()(apiController: ApiController) extends SimpleRouter {
    def routes: Routes = {
        new AbstractPartialFunction[RequestHeader, Handler] {
            override def applyOrElse[A <: RequestHeader, B >: Handler](request: A, default: (A) => B): B = {
                val handler = request.attrs.get(Router.Attrs.HandlerDef)
                (request.method, request.path) match {
                    case ("GET", "/0") => apiController.getArticle("1")
                }
            }

            override def isDefinedAt(x: RequestHeader): Boolean = true
        }
    }
}