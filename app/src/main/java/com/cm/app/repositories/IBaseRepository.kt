package com.cm.app.repositories

import org.jsoup.nodes.Element

interface IBaseRepository {
    fun getId(element: Element) : String
    fun getName(element: Element) : String
    fun getUrl(element: Element) : String
}