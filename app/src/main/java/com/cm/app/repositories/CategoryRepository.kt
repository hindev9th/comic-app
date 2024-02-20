package com.cm.app.repositories

import com.cm.app.models.Category
import com.cm.app.utils.DetailHelper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class CategoryRepository:ICategoryRepository {
    override fun getList(document: Document): ArrayList<Category> {
        val categories: ArrayList<Category> = arrayListOf()
        val list = DetailHelper.getCategories(document)
        list.forEach{res ->
            val name = getName(res)
            val url = getUrl(res)

            val category = Category(url,name)
            categories.add(category)
        }
        return categories
    }

    override fun getId(element: Element): String {
        TODO("Not yet implemented")
    }

    override fun getName(element: Element): String {
        return DetailHelper.getCategoryName(element)
    }

    override fun getUrl(element: Element): String {
        return DetailHelper.getCategoryUrl(element)
    }
}