package controllers

import javax.inject._

import models.Blog
import play.api.mvc._
import play.api.libs.json.Json
import utils.validation

@Singleton
class ApiController @Inject() extends Controller {
    def getBlog = Action { implicit request =>
        val offset = if (validation.isNumber(request.getQueryString("offset"))){
            request.getQueryString("offset").get.toInt
        } else {
            0
        }

        val limit = if (validation.isNumber(request.getQueryString("limit"))){
            request.getQueryString("limit").get.toInt
        } else {
            10
        }

        val data = Blog.find(offset, limit)

        val json = Json.toJson(data)
        Ok(Json.stringify(json))
    }
}
