package controllers

import javax.inject.{Inject, Singleton}

import models.ArticleImpl
import play.api.cache.AsyncCacheApi
import play.api.mvc.{AnyContent, InjectedController, Request}
import play.api.cache.NamedCache
import utils.ArticleCache

@Singleton
class ArticleController @Inject()(implicit @NamedCache("articles") cache: AsyncCacheApi, article_impl: ArticleImpl) extends InjectedController {
    def find(id: String) = Action { implicit request: Request[AnyContent] =>
        ArticleCache.get(id) match {
            case Some(article) => {
                Redirect(article.url)
            }
            case None => {
                article_impl.find(id) match {
                    case Some(article_db) => {
                        ArticleCache.set(article_db)
                        Redirect(article_db.url)
                    }
                    case None => NotFound(views.html.template.notfound("Article is not found"))
                }
            }
        }
    }
}