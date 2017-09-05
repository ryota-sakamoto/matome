package utils

import models.User
import play.cache.CacheApi

object UserCache {
    def get(uuid: String)(implicit cache: CacheApi): Option[User] = {
        Option(cache.get(uuid))
    }

    def set(u: User)(implicit cache: CacheApi): String = {
        val uuid = Security.generateUUID()
        cache.set(uuid, u, 3600)
        uuid
    }

    def update(uuid: String, u: User)(implicit cache: CacheApi): Unit = {
        cache.set(uuid, u, 3600)
    }

    def remove(uuid: String)(implicit cache: CacheApi) = {
        cache.remove(uuid)
    }
}