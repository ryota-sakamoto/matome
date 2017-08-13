package utils

import models.User
import play.cache.CacheApi

object UserCache {
    def get(cache: CacheApi, uuid: String): Option[User] = {
        Option(cache.get(uuid))
    }

    def set(cache: CacheApi, u: User): String = {
        val uuid = Security.generateUUID()
        cache.set(uuid, u, 3600)
        uuid
    }

    def remove(cache: CacheApi, uuid: String) = {
        cache.remove(uuid)
    }
}