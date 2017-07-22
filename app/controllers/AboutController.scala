package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class AboutController @Inject() extends Controller {
  def index = Action {
    Ok(views.html.about.index())
  }
}
