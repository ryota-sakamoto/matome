package controllers

import javax.inject._

import models.Article
import play.api.mvc._
import play.api.libs.json.Json
import utils.Validation

@Singleton
class ApiController @Inject() extends Controller {
    def getArticle = Action { implicit request =>
        val offset = if (Validation.isNumber(request.getQueryString("offset"))){
            request.getQueryString("offset").get.toInt
        } else {
            0
        }

        val limit = if (Validation.isNumber(request.getQueryString("limit"))){
            request.getQueryString("limit").get.toInt
        } else {
            10
        }

        val data = Article.find(offset, limit)

        val json = Json.toJson(data)
        Ok(Json.stringify(json))
    }
}
