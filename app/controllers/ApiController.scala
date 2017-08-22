package controllers

import javax.inject._

import models.Article
import play.api.mvc._
import play.api.libs.json.Json

@Singleton
class ApiController @Inject() extends Controller {
    def getArticle = Action { implicit request =>
        (for {
            o <- request.getQueryString("offset")
            l <- request.getQueryString("limit")
        } yield (o.toInt, l.toInt) match {
            case (offset, limit) => Article.find(offset, limit)
            case _ => Article.find(0, 0)
        }) match {
            case Some(r) => {
                val json = Json.toJson(r)
                Ok(Json.stringify(json))
            }
            case None => BadRequest("bad request")
        }
    }
}
