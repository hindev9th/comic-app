package com.cm.app.utilities

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Constants {
    companion object {
        const val BASE_COMIC_URL = "https://www.nettruyenup.vn/";

        fun getDataComic(url: String): Document {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .followRedirects(true)
                .timeout(5000)  // Set timeout in milliseconds
                .get()
            return Jsoup.parse(doc.html())
        }
    }

}