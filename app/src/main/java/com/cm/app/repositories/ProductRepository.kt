package com.cm.app.repositories

import com.cm.app.models.Chapter
import com.cm.app.models.Product
import com.cm.app.utils.ProductHelper
import com.cm.app.utils.Constants
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ProductRepository : IProductRepository {
    override fun getList(document: Document): ArrayList<Product> {
        val products : ArrayList<Product> = arrayListOf();
        val listElements = ProductHelper.getListComic(document)
        listElements.forEach { res ->
            val id = getId(res)
            val name = getName(res)
            val url = getUrl(res).replace(Constants.BASE_COMIC_URL,"")
            val image = getUrlImage(res).replace(Constants.getBaseImageUrl(),"")


            val chapFirstId = getChapFirstId(res)
            val chapFirst = getChapFirstName(res)
            val chapUrlFirst = getChapFirstUrl(res).replace(Constants.BASE_COMIC_URL,"")

            val chapSecondId = getChapSecondId(res)
            val chapSecond = getChapSecondName(res)
            val chapUrlSecond = getChapSecondUrl(res).replace(Constants.BASE_COMIC_URL,"")

            val chapter1 = Chapter(chapFirstId,chapUrlFirst,chapFirst,"","")
            val chapter2 = Chapter(chapSecondId,chapUrlSecond,chapSecond,"","")

            val product = Product(id,name,chapter1,chapter2,url,image)

            products.add(product)
        }
        return products
    }

    override fun getId(element: Element): String {
        return ProductHelper.getComicId(element)
    }

    override fun getName(element: Element): String {
        return ProductHelper.getComicName(element)
    }

    override fun getUrl(element: Element): String {
        return ProductHelper.getComicUrl(element)
    }

    override fun getUrlImage(element: Element): String {
        return ProductHelper.getImageComicUrl(element);
    }

    override fun getChapFirstId(element: Element): String {
        return ProductHelper.getComicChapter1Id(element)
    }

    override fun getChapFirstName(element: Element): String {
        return ProductHelper.getComicChapter1(element)
    }

    override fun getChapFirstUrl(element: Element): String {
        return ProductHelper.getComicChapter1Url(element)
    }

    override fun getChapSecondId(element: Element): String {
        return ProductHelper.getComicChapter2Id(element)
    }

    override fun getChapSecondName(element: Element): String {
        return ProductHelper.getComicChapter2(element)
    }

    override fun getChapSecondUrl(element: Element): String {
        return ProductHelper.getComicChapter2Url(element)
    }
}