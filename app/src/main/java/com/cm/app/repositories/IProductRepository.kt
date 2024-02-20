package com.cm.app.repositories

import com.cm.app.models.Product
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface IProductRepository : IBaseRepository{
    fun getList(document: Document) : ArrayList<Product>
    fun getUrlImage(element: Element) : String
    fun getChapFirstId(element: Element) : String
    fun getChapFirstName(element: Element) : String
    fun getChapFirstUrl(element: Element) : String
    fun getChapSecondId(element: Element) : String
    fun getChapSecondName(element: Element) : String
    fun getChapSecondUrl(element: Element) : String
}