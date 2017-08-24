package controllers

import javax.inject._

import models.Article
import play.api.mvc._
import play.api.libs.json.Json
import utils.Validation

@Singleton
class ApiController @Inject() extends Controller {
    def getArticle = Action { implicit request =>
        if (Validation.validate("offset" -> 'int, "limit" -> 'int)) {
            val offset = request.getQueryString("offset").get.toInt
            val limit = request.getQueryString("limit").get.toInt
            val articles = Article.find(offset, limit)
            Ok(Json.stringify(Json.toJson(articles)))
        } else {
            BadRequest("bad request")
        }
    }
}
