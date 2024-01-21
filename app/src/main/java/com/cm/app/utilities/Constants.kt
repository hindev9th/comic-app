package com.cm.app.utilities

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Constants {
    companion object {
        const val BASE_COMIC_URL = "https://www.nettruyenclub.com/";

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
    }

}