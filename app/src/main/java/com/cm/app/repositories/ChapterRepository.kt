package com.cm.app.repositories

import com.cm.app.models.Chapter
import com.cm.app.utils.DetailHelper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ChapterRepository:IChapterRepository {
    override fun getList(document: Document): ArrayList<Chapter> {
        val chapters : ArrayList<Chapter> = arrayListOf()
        val list = DetailHelper.getListChapter(document)
        list.forEach{res ->
            val id = getId(res)
            val name = getName(res)
            val time = getTimeAgo(res)
            val views = getView(res)
            val url = getUrl(res)

            val chapter = Chapter(id,url,name,time,views)
            chapters.add(chapter)
        }
        return chapters
    }

    override fun getView(element: Element): String {
        return DetailHelper.getChapterViews(element)
    }

    override fun getTimeAgo(element: Element): String {
        return DetailHelper.getChapterTimeAgo(element)
    }

    override fun getId(element: Element): String {
        return DetailHelper.getChapterId(element)
    }

    override fun getName(element: Element): String {
        return DetailHelper.getChapterName(element)
    }

    override fun getUrl(element: Element): String {
        return DetailHelper.getChapterUrl(element)
    }
}