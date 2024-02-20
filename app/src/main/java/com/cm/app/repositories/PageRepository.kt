package com.cm.app.repositories

import com.cm.app.models.Page
import com.cm.app.utils.ChapterHelper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class PageRepository:IPageRepository {
    override fun getList(document: Document): ArrayList<Page> {
        val pages: ArrayList<Page> = arrayListOf()
        val list = ChapterHelper.getChapterListImage(document)
        list.forEach{res ->
            val url = getUrl(res)
            val page = Page(url)

            pages.add(page)
        }
        return pages;
    }

    override fun getId(element: Element): String {
        TODO("Not yet implemented")
    }

    override fun getName(element: Element): String {
        TODO("Not yet implemented")
    }

    override fun getUrl(element: Element): String {
        return ChapterHelper.getChapterImageUrl(element)
    }
}