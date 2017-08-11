package utils

import java.security.MessageDigest
import java.util.UUID.randomUUID

import play.api.mvc.{AnyContent, Request}

object Security {
    val session_name = "user_uuid"

    def md5(text: String): String = {
        MessageDigest.getInstance("MD5").digest(text.getBytes).map("%02x".format(_)).mkString
    }

    def generateUUID(): String = {
        randomUUID.toString.replace("-", "")
    }

    def getSessionUUID(request: Request[AnyContent]): String = {
        request.session.get(session_name) match {
            case Some(uuid) => uuid.toString
            case None => ""
        }
    }
}