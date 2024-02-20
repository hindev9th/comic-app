package com.cm.app.repositories

import com.cm.app.models.Category
import org.jsoup.nodes.Document

interface ICategoryRepository: IBaseRepository {
    fun getList(document: Document):ArrayList<Category>
}