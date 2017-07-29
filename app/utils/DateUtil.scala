package utils

import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {
    def convertToDate(str_date: String): Date = {
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
        format.parse(str_date)
    }
}