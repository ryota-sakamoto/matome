package utils

import models.User
import play.api.cache.AsyncCacheApi

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object UserCache {
    def get(uuid:String)(implicit cache: AsyncCacheApi): Option[User] = {
        val user = cache.get[User](uuid)
        Await.ready(user, Duration.Inf)
        user.value.get.get
    }

    def set(u: User)(implicit cache: AsyncCacheApi): String = {
        val uuid = Security.generateUUID()
        cache.set(uuid, u, Duration("3600"))
        uuid
    }

    def update(uuid: String, u: User)(implicit cache: AsyncCacheApi): Unit = {
        cache.set(uuid, u, Duration("3600"))
    }

    def remove(uuid: String)(implicit cache: AsyncCacheApi) = {
        cache.remove(uuid)
    }
}