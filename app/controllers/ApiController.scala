package controllers

import javax.inject._

import models.{Article, Blog}
import play.api.mvc._
import play.api.libs.json.Json
import utils.Validation

@Singleton
class ApiController @Inject() extends Controller {
    def getArticle(id: String) = Action { implicit request =>
        if (Validation.validate("offset" -> 'int, "limit" -> 'int)) {
            val offset = request.getQueryString("offset").get.toInt
            val limit = request.getQueryString("limit").get.toInt

            val articles = Article.findByBlogId(id, limit, offset)
            Ok(Json.stringify(Json.toJson(articles))).as("application/json")
        } else {
            BadRequest("bad request")
        }
    }
}
