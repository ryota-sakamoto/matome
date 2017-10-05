package handlers

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import play.api.Logger;

@Singleton
class ErrorHandler extends HttpErrorHandler {
    val prefix = "[ErrorHandler]"

    def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
        Logger.error(s"$prefix $statusCode ${request.target.path} $message")
        Future.successful(
            NotFound(views.html.template.notfound("This Page is Not Found"))
        )
    }

    def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
        Logger.error(s"$prefix ${exception.getStackTrace.mkString("\n")}")
        Future.successful(
            InternalServerError(views.html.template.notfound("Server Error"))
        )
    }
}