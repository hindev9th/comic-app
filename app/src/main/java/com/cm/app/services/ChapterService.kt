package com.cm.app.services

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ChapterService {
    companion object{
        fun getChapterListImage(doc: Document): Elements {
            // Kotlin's type-safe selectors for cleaner querying
            return doc.select("div.reading-detail.box_doc div.page-chapter")
        }

        fun getChapterImageUrl(element: Element): String {
            val img = element.selectFirst("img")
            return img?.attr("src") ?: ""
        }
    }
}