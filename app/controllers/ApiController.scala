package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json

@Singleton
class ApiController @Inject() extends Controller {
    def getBlog = Action {
        val _type = Array("ameblo", "lineblog", "livedoor")
        val n = new java.util.Random().nextInt(10)
        val data = Seq(
            Json.toJson(Map(
                "type" -> Json.toJson(_type(n % 3)),
                "name" -> Json.toJson((n + n * n) / (n + 1)),
                "description" -> Json.toJson("kick api %d".format(n)),
                "update_date" -> Json.toJson(new java.util.Date())
            ))
        )

        val json = Json.toJson(data)
        Ok(Json.stringify(json))
    }
}
