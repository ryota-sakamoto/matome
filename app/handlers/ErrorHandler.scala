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

    def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
        Logger.error(s"$prefix $message")
        Future.successful(
            NotFound(views.html.template.notfound())
        )
    }

    def onServerError(request: RequestHeader, exception: Throwable) = {
        Logger.error(s"$prefix $exception")
        Future.successful(
            NotFound(views.html.template.notfound())
        )
    }
}