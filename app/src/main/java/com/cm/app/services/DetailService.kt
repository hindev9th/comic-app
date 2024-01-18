package com.cm.app.services

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.regex.Pattern

class DetailService {
    companion object {
        fun getView(doc: Document): String {
            val view =
                doc.select("article#item-detail div.detail-info div.row div.col-info ul.list-info li")[3].select(
                    "p.col-xs-8"
                )
            return view.text() ?: ""
        }

        fun getDescription(doc: Document): String {
            val view =
                doc.select("article#item-detail div.detail-content div.about")[0]
            return view.text() ?: ""
        }

        fun getAuthor(doc: Document): String {
            val author =
                doc.select("article#item-detail div.detail-info div.row div.col-info ul.list-info li.author p.col-xs-8")
            return author.text() ?: ""
        }

        fun getStatus(doc: Document): String {
            val status =
                doc.select("article#item-detail div.detail-info div.row div.col-info ul.list-info li.status p.col-xs-8")
            return status.text() ?: ""
        }

        fun getCategories(doc: Document): Elements {
            return doc.select("article#item-detail div.detail-info div.row div.col-info ul.list-info li.kind p.col-xs-8 a")
        }

        fun getCategoryName(element: Element): String {
            return element.text() ?: "" // Handle potential null values
        }

        fun getCategoryUrl(element: Element): String {
            return element.attr("href") ?: "" // Handle potential null values
        }

        fun getListChapter(doc: Document): Elements? {
            // Kotlin supports type-safe selectors for cleaner querying
            return doc.select("div#nt_listchapter nav ul li")
        }

        fun getChapterUrl(element: Element): String {
            val div = element.selectFirst("div")
            val a = div?.selectFirst("a")
            return a?.attr("href") ?: ""
        }

        fun getChapterName(element: Element): String {
            val div = element.selectFirst("div")
            return div?.text() ?: "" // Handle potential null values
        }

        fun getChapterTimeAgo(element: Element): String {
            val divs = element.select("div")
            return divs.getOrNull(1)?.text() ?: "" // Assuming time is in the second div
        }

        fun getChapterViews(element: Element): String {
            val divs = element.select("div")
            return divs.getOrNull(2)?.text() ?: "" // Assuming views are in the third div
        }

        fun getChapterUrlKey(element: Element): String {
            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(getChapterName(element))
            matcher.find()
            val chapterNumber = matcher.group()
            val urlKey = "chap-$chapterNumber"
            return urlKey.replace("\\s+".toRegex(), "")
        }


        fun getChapterId(element: Element): String? {
            val div = element.selectFirst("div")
            val a = div?.selectFirst("a")
            return a?.attr("data-id")
        }

    }
}