package utils

import models.Article
import play.api.cache.AsyncCacheApi

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ArticleCache {
    def get(id: String)(implicit cache: AsyncCacheApi): Option[Article] = {
        val article = cache.get[Article](id)
        Await.ready(article, Duration.Inf)
        article.value.get.get
    }

    def set(article: Article)(implicit cache: AsyncCacheApi) = {
        cache.set(article.id, article)
    }
}