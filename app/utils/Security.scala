package utils

import java.security.MessageDigest
import java.util.UUID.randomUUID

object Security {
    def md5(text: String): String = {
        MessageDigest.getInstance("MD5").digest(text.getBytes).map("%02x".format(_)).mkString
    }

    def generateUUID(): String = {
        randomUUID.toString.replace("-", "")
    }
}