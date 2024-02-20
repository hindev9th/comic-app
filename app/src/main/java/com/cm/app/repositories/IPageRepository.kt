package com.cm.app.repositories

import com.cm.app.models.Page
import org.jsoup.nodes.Document

interface IPageRepository : IBaseRepository{
    fun getList(document: Document) : ArrayList<Page>
}