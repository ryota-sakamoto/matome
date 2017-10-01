package controllers

import javax.inject._

import forms.SearchForm
import models.elasticsearch.Elastic
import play.api.cache.AsyncCacheApi
import play.api.mvc._
import utils.{Security, UserCache}

@Singleton
class SearchController @Inject()(implicit cache: AsyncCacheApi, messagesAction: MessagesActionBuilder) extends InjectedController {
    def index = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val user_uuid = Security.getSessionUUID(request)
        val user = UserCache.get(user_uuid)
        Ok(views.html.search.index(user, SearchForm.form))
    }

    def result = Action { implicit request: Request[AnyContent] =>
        val user_uuid = Security.getSessionUUID(request)
        val user = UserCache.get(user_uuid)
        val word = request.getQueryString("word")
        val query = word match {
            case Some(q) => q.split(' ')
            case None => Array.empty[String]
        }

        val result = query.map { q =>
            Elastic.find(q)
        } flatMap { r =>
            r
        }

        Ok(views.html.search.result(user, query.mkString(" "), result))
    }
}