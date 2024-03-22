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
import android.widget.Toast
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var imageBookmark: ImageView
    private lateinit var description: TextView
    private lateinit var frameLayoutBack: FrameLayout
    private lateinit var firstChapter: Chapter
    private lateinit var historyDao: HistoryDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        progressBarImage = findViewById(R.id.progressBarImage)
        progressBar = findViewById(R.id.progressBar)
        recyclerCategories = findViewById(R.id.recyclerCategories)
        recyclerChapters = findViewById(R.id.recyclerChapters)
        chapterModelList = arrayListOf()
        categoryModelList = arrayListOf()
        author = findViewById(R.id.textNameAuthor)
        textStatus = findViewById(R.id.textStatus)
        view = findViewById(R.id.textView)
        description = findViewById(R.id.textContentDescription)
        textRead = findViewById(R.id.textRead)
        imageBookmark = findViewById(R.id.imageBookmark)
        frameLayoutBack = findViewById(R.id.frameLayoutBack)

        historyDao = HistoryDao(this)
        favoriteDao = FavoriteDao(this)

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
        loadData()
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
            Constants.saveHistory(this,product,firstChapter)

            val intent = Intent(this@DetailActivity, ReadActivity::class.java)
            intent.putExtra("chapter", ct)
            intent.putExtra("product", pr)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        imageBookmark.setOnClickListener {
            toggleLike()
        }

        frameLayoutBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadData() {
        val image = findViewById<ImageView>(R.id.imageProduct)
        val name = findViewById<TextView>(R.id.textName)

        name.text = product.name

        Glide.with(this).load(Constants.getBaseImageUrl() + product.urlImage)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
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
            Toast.makeText(this,"Xóa khỏi danh sách thành công!",Toast.LENGTH_SHORT).show()
        } else {
            val favorite = Favorite(
                product.id,
                product.name,
                product.url,
                product.urlImage,
                firstChapter.chapterId,
                firstChapter.name,
                firstChapter.url,
                Constants.getCurrentDateTime()
            )
            favoriteDao.insertFavorite(favorite)
            loadFavorite(true)
            Toast.makeText(this,"Thêm vào danh sách thành công!",Toast.LENGTH_SHORT).show()
        }
    }

    fun loadFavorite(boolean: Boolean) {
        if (boolean) {
            imageBookmark.setImageResource(R.drawable.ic_bookmark_bold)
        } else {
            imageBookmark.setImageResource(R.drawable.ic_bookmark_none)
        }
    }
}