package com.cm.app.services

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.Normalizer
import java.util.regex.Pattern

class ProductService {
    companion object{
        fun getListComic(doc: Document): Elements? {
            // Kotlin supports type-safe selectors for cleaner querying
            return doc.select("div.ModuleContent div.items div.row div.item figure")
        }

        fun getImageComicUrl(element: Element): String {
            val imageDiv = element.selectFirst("div")
            val image = imageDiv?.selectFirst("img")
            val imageUrl = image?.attr("data-original")
            return ("https:$imageUrl") // Handle potential null values
        }

        fun getComicName(element: Element): String {
            val figcaption = element.selectFirst("figcaption")
            val h3 = figcaption?.selectFirst("h3")
            return h3?.text() ?: "" // Handle potential null values
        }

        fun getComicUrl(element: Element): String {
            val div = element.selectFirst("div")
            val a = div?.selectFirst("a")
            return a?.attr("href") ?: "" // Handle potential null values
        }

        fun getComicUrlKey(element: Element): String {
            // Convert the string to lowercase letters
            var string = getComicName(element)
            val unaccentedString = Normalizer.normalize(string, Normalizer.Form.NFD)
                .replace("[^\\p{ASCII}]".toRegex(), "")
            string = string.replace(Regex("[?\'~^:,/\\[\\].!]"), "")
            string = string.replace(Regex(" - |! "), " ")
            // Replace all spaces with "-" characters
            string = string.replace(" ", "-")
            string = string.toLowerCase()
            string = string.trim('-')
            return string.replace("\\s+".toRegex(), "")
        }

        fun getComicId(element: Element): String? {
            val figcaption = element.selectFirst("figcaption")
            val ul = figcaption.selectFirst("ul")
            return ul?.attr("data-id")
        }


        fun getComicChapter1(element: Element): String {
            val figcaption = element.selectFirst("figcaption")
            val ul = figcaption.selectFirst("ul")
            val a = ul?.selectFirst("li")?.selectFirst("a")
            val stringChap = a?.text() ?: ""

            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(stringChap)
            matcher.find()
            return matcher.group()
        }
        fun getComicChapter1Url(element: Element): String {
            val figcaption = element.selectFirst("figcaption")
            val ul = figcaption.selectFirst("ul")
            val url = ul?.selectFirst("li")?.selectFirst("a")?.attr("href")

            return url ?: ""
        }

        fun getComicChapter2(element: Element): String {
            val figcaption = element.selectFirst("figcaption")
            val ul = figcaption?.selectFirst("ul")
            val a = ul?.select("li")?.get(1)?.selectFirst("a")
            val stringChap = a?.text() ?: ""

            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(stringChap)
            matcher.find()
            return matcher.group()
        }
        fun getComicChapter2Url(element: Element): String {
            val figcaption = element.selectFirst("figcaption")
            val ul = figcaption.selectFirst("ul")
            val url = ul?.select("li")?.get(1)?.selectFirst("a")?.attr("href")

            return url ?: ""
        }
    }
}