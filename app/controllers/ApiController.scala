package controllers

import javax.inject._

import models.ArticleImpl
import play.api.mvc._
import play.api.libs.json.Json
import utils.Validation

@Singleton
class ApiController @Inject()(article: ArticleImpl) extends InjectedController {
    def getArticle(id: String) = Action { implicit request: Request[AnyContent] =>
        if (Validation.validate("offset" -> 'int, "limit" -> 'int)) {
            val offset = request.getQueryString("offset").getOrElse("0").toInt
            val limit = request.getQueryString("limit").getOrElse("0").toInt

            val articles = article.findByBlogId(id, limit, offset)
            Ok(Json.stringify(Json.toJson(articles))).as("application/json")
        } else {
            BadRequest("bad request")
        }
    }
}
