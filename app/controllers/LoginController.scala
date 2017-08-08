package controllers

import javax.inject._

import play.api.mvc._
import play.api.cache._
import play.cache.CacheApi

@Singleton
class LoginController @Inject()(cache: CacheApi) extends Controller {
    def index = Action {
        Ok("")
    }

    def login = Action {
        Ok("")
    }
}
