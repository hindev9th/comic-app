package com.cm.app.repositories

import com.cm.app.models.Chapter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface IChapterRepository : IBaseRepository{
    fun getList(document: Document) : ArrayList<Chapter>
    suspend fun getChapters(comicId : String) : ArrayList<Chapter>
    fun getView(element: Element) : String
    fun getTimeAgo(element: Element) :String
}