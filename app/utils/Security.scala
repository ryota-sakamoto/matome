package utils

import java.util.UUID.randomUUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import play.api.mvc.{AnyContent, Request}

object Security {
    val session_name = "user_uuid"

    def encrypt(text: String): String = {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(new SecretKeySpec(text.getBytes, "HmacSHA256"))
        mac.doFinal().map("%02x".format(_)).mkString
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