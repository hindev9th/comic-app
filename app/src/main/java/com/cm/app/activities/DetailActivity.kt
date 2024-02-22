package com.cm.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cm.app.models.Category
import com.cm.app.models.Chapter
import com.cm.app.R
import com.cm.app.repositories.CategoryRepository
import com.cm.app.repositories.ChapterRepository
import com.cm.app.repositories.ICategoryRepository
import com.cm.app.repositories.IChapterRepository
import com.cm.app.adapters.CategoryAdapter
import com.cm.app.adapters.ChapterAdapter
import com.cm.app.data.database.dao.FavoriteDao
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.Favorite
import com.cm.app.models.Product
import com.cm.app.utils.DetailHelper
import com.cm.app.utils.Constants
import com.google.gson.Gson
import org.jsoup.nodes.Document

class DetailActivity : AppCompatActivity() {

    private lateinit var progressBarImage: FrameLayout
    private lateinit var progressBar: FrameLayout
    private lateinit var doc: Document
    private lateinit var recyclerCategories: RecyclerView
    private lateinit var recyclerChapters: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var chapterModelList: ArrayList<Chapter>
    private lateinit var chapterModelListBase: ArrayList<Chapter>
    private lateinit var categoryModelList: ArrayList<Category>
    private lateinit var iChapterRepository: IChapterRepository
    private lateinit var iCategoryRepository: ICategoryRepository
    private lateinit var author: TextView
    private lateinit var textStatus: TextView
    private lateinit var view: TextView
    private lateinit var textRead: TextView
    private lateinit var textFavorite: TextView
    private lateinit var description: TextView
    private lateinit var firstChapter: Chapter
    private lateinit var historyDao: HistoryDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        this.progressBarImage = findViewById(R.id.progressBarImage)
        this.progressBar = findViewById(R.id.progressBar)
        this.recyclerCategories = findViewById(R.id.recyclerCategories)
        this.recyclerChapters = findViewById(R.id.recyclerChapters)
        this.chapterModelList = arrayListOf()
        this.categoryModelList = arrayListOf()
        this.author = findViewById(R.id.textNameAuthor)
        this.textStatus = findViewById(R.id.textStatus)
        this.view = findViewById(R.id.textView)
        this.description = findViewById(R.id.textContentDescription)
        this.textRead = findViewById(R.id.textRead)
        this.textFavorite = findViewById(R.id.textFavorite)

        this.historyDao = HistoryDao(this)
        this.favoriteDao = FavoriteDao(this)


        val gson = Gson()
        this.product =
            gson.fromJson(intent.getStringExtra("product").toString(), Product::class.java)

        this.iChapterRepository = ChapterRepository()
        this.iCategoryRepository = CategoryRepository()

        this.categoryAdapter = CategoryAdapter(this.categoryModelList)
        this.recyclerCategories.adapter = this.categoryAdapter

        this.chapterAdapter = ChapterAdapter(this.chapterModelList, this.product, historyDao)
        this.recyclerChapters.adapter = this.chapterAdapter

        this.listerEvent()
        this.loadData()
        loadFavorite(favoriteDao.getById(product.id) != null)
    }

    override fun onResume() {
        super.onResume()
        this.loadData()
    }

    @SuppressLint("NotifyDataSetChanged", "ResourceType")
    private fun listerEvent() {
//        val home = findViewById<ImageView>(R.id.imageHome)
//        val search = findViewById<ImageView>(R.id.imageSearch)
        val filter = findViewById<ImageView>(R.id.imageFilter)
        val image: ImageView = findViewById(R.id.imageProduct)

        val gson = Gson()
        val pr = gson.toJson(product)

        filter.setOnClickListener {
            this.chapterModelList.reverse()
            this.chapterAdapter.notifyDataSetChanged()
        }

        textRead.setOnClickListener {
            val ct = gson.toJson(firstChapter)

            val intent = Intent(this@DetailActivity, ReadActivity::class.java)
            intent.putExtra("chapter", ct)
            intent.putExtra("product", pr)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        textFavorite.setOnClickListener{
            toggleLike()
        }


    }

    fun loadData() {
        val image: ImageView = findViewById(R.id.imageProduct)
        val name: TextView = findViewById(R.id.textName)

        name.text = this.product.name

        Glide.with(this).load(Constants.getBaseImageUrl() + this.product.urlImage).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBarImage.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBarImage.visibility = View.GONE
                return false
            }
        }).into(image)

        val task = MyNetworkTask()
        task.execute(Constants.BASE_COMIC_URL + this.product.url)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getData() {
        val author = DetailHelper.getAuthor(this.doc)
        val status = DetailHelper.getStatus(this.doc)
        val view = DetailHelper.getView(this.doc)
        val description = DetailHelper.getDescription(this.doc)

        this.author.text = author
        this.textStatus.text = status
        this.view.text = view
        this.description.text = description

        this.categoryModelList = this.iCategoryRepository.getList(this.doc);
        this.categoryAdapter = CategoryAdapter(this.categoryModelList)
        this.recyclerCategories.adapter = this.categoryAdapter

        this.chapterModelList = this.iChapterRepository.getList(this.doc)
        this.firstChapter = this.chapterModelList[this.chapterModelList.size - 1];
        this.chapterModelListBase = this.chapterModelList
        this.chapterAdapter = ChapterAdapter(this.chapterModelList, this.product, this.historyDao)
        this.recyclerChapters.adapter = this.chapterAdapter

        this.progressBar.visibility = View.GONE
    }


    inner class MyNetworkTask : AsyncTask<String, Void, Document>() {

        override fun doInBackground(vararg params: String): Document {
            try {
                doc = Constants.getDataComic(params[0])
            } catch (e: Exception) {
                cancel(true);
            }
            return doc
        }

        override fun onPostExecute(result: Document) {
            getData()
        }
    }

    fun toggleLike() {
        if (favoriteDao.getById(product.id) != null) {
            favoriteDao.deleteFavoriteById(product.id)
            loadFavorite(false)
        } else {
            val favorite = Favorite(
                product.id,
                product.name,
                product.url,
                product.urlImage,
                firstChapter.id,
                firstChapter.name,
                firstChapter.url,
                Constants.getCurrentDateTime()
            )
            favoriteDao.insertFavorite(favorite)
            loadFavorite(true)
        }

    }

    fun loadFavorite(boolean: Boolean) {
        if (boolean) {
            textFavorite.text = getString(R.string.unlike)
            textFavorite.setTextColor(getColor(R.color.head_color))
            textFavorite.background = getDrawable(R.drawable.border_chapter)
            val drawable: Drawable? = getDrawable(R.drawable.ic_favorite)
            val color = getColor(R.color.background)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            textFavorite.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } else {
            textFavorite.text = getString(R.string.them_yeu_thich)
            textFavorite.setTextColor(getColor(R.color.white_text))
            textFavorite.background = getDrawable(R.drawable.background_chapter)
            val drawable: Drawable? = getDrawable(R.drawable.ic_favorite)
            val color = getColor(R.color.white_text)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            textFavorite.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }
}