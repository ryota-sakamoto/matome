package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class SettingController @Inject() extends Controller {
  def index = Action {
    Ok(views.html.settings.index())
  }
}
