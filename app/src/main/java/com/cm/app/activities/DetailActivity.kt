package com.cm.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.cm.app.R
import com.cm.app.adapters.CategoryAdapter
import com.cm.app.adapters.ChapterAdapter
import com.cm.app.models.CategoryModel
import com.cm.app.models.ChapterModel
import com.cm.app.services.DetailService
import com.cm.app.services.ProductService
import com.cm.app.utilities.Constants
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.Serializable

class DetailActivity : AppCompatActivity() {

    private lateinit var progressBarImage: FrameLayout
    private lateinit var progressBar: FrameLayout
    private lateinit var doc: Document
    private lateinit var recyclerCategories: RecyclerView
    private lateinit var recyclerChapters: RecyclerView
    private lateinit var chapterAdapter : ChapterAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var chapterModelList : ArrayList<ChapterModel>
    private lateinit var chapterModelListBase : ArrayList<ChapterModel>
    private lateinit var categoryModelList : ArrayList<CategoryModel>
    private lateinit var chapterElements: Elements
    private lateinit var categoryElements: Elements
    private lateinit var author: TextView
    private lateinit var textStatus: TextView
    private lateinit var view: TextView
    private lateinit var description: TextView
    private lateinit var firstChapter : ChapterModel
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        this.progressBarImage = findViewById(R.id.progressBarImage)
        this.progressBar = findViewById(R.id.progressBar)
        this.recyclerCategories = findViewById(R.id.recyclerCategories)
        this.recyclerChapters = findViewById(R.id.recyclerChapters)
        this.chapterModelList = arrayListOf<ChapterModel>()
        this.categoryModelList = arrayListOf<CategoryModel>()
        this.author = findViewById(R.id.textNameAuthor)
        this.textStatus = findViewById(R.id.textStatus)
        this.view = findViewById(R.id.textView)
        this.description = findViewById(R.id.textContentDescription)


        categoryAdapter = CategoryAdapter(categoryModelList)
        recyclerCategories.adapter = categoryAdapter

        chapterAdapter = ChapterAdapter(chapterModelList)
        recyclerChapters.adapter = chapterAdapter

        this.url = intent.getStringExtra("url").toString()
        this.listerEvent()
        this.loadData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listerEvent() {
        val home = findViewById<ImageView>(R.id.imageHome)
        val search = findViewById<ImageView>(R.id.imageSearch)
        val read = findViewById<TextView>(R.id.textRead)
        val filter = findViewById<ImageView>(R.id.imageFilter)

        home.setOnClickListener {
            onBackPressed()
        }

        search.setOnClickListener{
            val intent = Intent(this@DetailActivity,SearchActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        filter.setOnClickListener{
            this.chapterModelList.reverse()
            this.chapterAdapter.notifyDataSetChanged()
        }

        read.setOnClickListener{
            val intent = Intent(this@DetailActivity,ReadActivity::class.java)
            intent.putExtra("url", this.firstChapter.url)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        this.chapterAdapter.setOnItemClickListener(object : ChapterAdapter.onItemClickListener{
            override fun onItemCLick(position: Int) {
                val intent = Intent(this@DetailActivity,ReadActivity::class.java)
                intent.putExtra("index",position)
                intent.putExtra("url", chapterModelList[position].url)
                intent.putExtra("docOld", doc.toString())
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

    }

    fun loadData() {
        val image: ImageView = findViewById(R.id.imageProduct)
        val name: TextView = findViewById(R.id.textName)


        name.text = intent.getStringExtra("name")

        Glide.with(this).load(intent.getStringExtra("image")).listener(object :
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
        task.execute(intent.getStringExtra("url"))
    }

    fun getData(){
        val author = DetailService.getAuthor(this.doc)
        val status = DetailService.getStatus(this.doc)
        val view = DetailService.getView(this.doc)
        val description = DetailService.getDescription(this.doc)

        this.author.text = author
        this.textStatus.text = status
        this.view.text = view
        this.description.text = description

        this.loadCategory()
        this.loadChapter()

        this.progressBar.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCategory(){
        val listCategories = DetailService.getCategories(this.doc)
        categoryElements = listCategories

        categoryElements.forEach{element ->
            val url = DetailService.getCategoryUrl(element)
            val name = DetailService.getCategoryName(element)
            val categoryModel = CategoryModel(
                url,
                name,
            )
            categoryModelList.add(categoryModel)
        }
        this.categoryAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadChapter(){
        val list = DetailService.getListChapter(this.doc)
        if (list != null) {
            chapterElements = list
        }

        chapterElements.forEach{element ->
            val url = DetailService.getChapterUrl(element)
            val name = DetailService.getChapterName(element)
            val time = DetailService.getChapterTimeAgo(element)
            val chapterModel = ChapterModel(
                url,
                name,
                time
            )
            chapterModelList.add(chapterModel)
        }
        this.firstChapter = this.chapterModelList[this.chapterModelList.size-1];
        this.chapterModelListBase = this.chapterModelList
        this.chapterAdapter.notifyDataSetChanged()

    }

    inner class MyNetworkTask : AsyncTask<String, Void, Document>() {

        override fun doInBackground(vararg params: String): Document {
            doc = Constants.getDataComic(params[0])
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (result != null) {
                getData()
            } else {
                // Handle error
            }
        }
    }
}