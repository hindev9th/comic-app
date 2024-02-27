package com.cm.app.utils

import android.content.Context
import android.preference.PreferenceManager
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.History
import com.cm.app.models.Chapter
import com.cm.app.models.Product
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern


class Constants {
    companion object {
        var BASE_COMIC_URL = "https://www.nettruyenbb.com/"
        val SCROLL_NEXT_CHAPTER = "ScrollNextChapter"

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

        fun getBaseImageUrl(): String {
            return BASE_COMIC_URL.replace("www", "st");
        }

        fun getCurrentDateTime(): String {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return formatter.format(calendar.time)
        }

        fun saveHistory(context: Context, product: Product, chapter: Chapter) {
            var historyDao = HistoryDao(context)

            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(chapter.name)
            matcher.find()

            val history = History(
                product.id,
                product.name,
                product.url.replace(Constants.BASE_COMIC_URL,""),
                product.urlImage.replace(getBaseImageUrl(),""),
                chapter.id,
                "Chapter "+matcher.group(),
                chapter.url.replace(Constants.BASE_COMIC_URL,""),
                getCurrentDateTime()
            )

            historyDao.insertOrUpdate(history)
        }
        fun saveHistory(context: Context, history: History) {
            var historyDao = HistoryDao(context)
            historyDao.insertOrUpdate(history)
        }
        fun setDefaults(key: String, value: String, context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.apply() // or editor.commit() in case you want to write data instantly
        }
        fun setDefaults(key: String, value: Boolean, context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putBoolean(key, value)
            editor.apply() // or editor.commit() in case you want to write data instantly
        }

        fun getDefaults(key: String, context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(key, null)
        }

        fun getBoolean(key: String, context: Context): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(key, false)
        }
    }

}