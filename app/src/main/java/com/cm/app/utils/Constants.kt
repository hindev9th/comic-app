package com.cm.app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Calendar
import java.text.SimpleDateFormat

class Constants {
    companion object {
        var BASE_COMIC_URL = "https://www.nettruyenbb.com/";

        fun getDataComic(url: String): Document {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .followRedirects(true)
                .timeout(5000)  // Set timeout in milliseconds
                .get()

            return Jsoup.parse(doc.html())
        }

        fun getBaseImageUrl() : String{
            return BASE_COMIC_URL.replace("www","st");
        }
        fun getCurrentDateTime(): String {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return formatter.format(calendar.time)
        }
    }

}