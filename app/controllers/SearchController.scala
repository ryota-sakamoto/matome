package controllers

import javax.inject._

import forms.SearchForm
import models.elasticsearch.Elastic
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.cache.CacheApi
import utils.Security

@Singleton
class SearchController @Inject()(cache: CacheApi, val messagesApi: MessagesApi) extends Controller  with I18nSupport {
    def index = Action { request =>
        val user_uuid = Security.getSessionUUID(request)
        Ok(views.html.search.index(cache, user_uuid, SearchForm.form))
    }

    def result = Action { request =>
        val user_uuid = Security.getSessionUUID(request)
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

        Ok(views.html.search.result(cache, user_uuid, query.mkString(" "), result))
    }
}